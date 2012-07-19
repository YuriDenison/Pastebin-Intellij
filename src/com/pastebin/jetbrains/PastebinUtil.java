package com.pastebin.jetbrains;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.net.HttpConfigurable;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yuri Denison
 * @date 12.07.12
 */
public class PastebinUtil {
  public static final String PASTEBIN = "Pastebin";
  public static final Icon ICON = IconLoader.getIcon("res/pastebin.png", PastebinUtil.class);
  private static final String API_KEY = "d42e7b2a43c3bd1149d2bbdae06730dd";
  private static final int API_PASTE_PRIVATE = 1;
  private static final String POST_URL = "http://pastebin.com/api/api_post.php";
  private static final String LOGIN_URL = "http://pastebin.com/api/api_login.php";

  private static HttpClient getClient() {
    final HttpClient client = new HttpClient();
    final HttpConfigurable proxyConfig = HttpConfigurable.getInstance();
    if (proxyConfig.USE_HTTP_PROXY) {
      client.getHostConfiguration().setProxy(proxyConfig.PROXY_HOST, proxyConfig.PROXY_PORT);
      if (proxyConfig.PROXY_AUTHENTICATION) {
        UsernamePasswordCredentials proxyCred = new UsernamePasswordCredentials(proxyConfig.PROXY_LOGIN, proxyConfig.getPlainProxyPassword());
        client.getState().setProxyCredentials(AuthScope.ANY, proxyCred);
      }
    }

    return client;
  }

  public static Element postRequest(@Nullable Map<String, String> params) throws IOException, JDOMException {
    final HttpClient client = getClient();
    final HttpMethod res = new PostMethod(POST_URL);
    if (params != null) {
      final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
      for (Map.Entry<String, String> entry : params.entrySet()) {
        pairs.add(new NameValuePair(entry.getKey(), entry.getValue()));
      }
      final NameValuePair[] arr = new NameValuePair[pairs.size()];
      pairs.toArray(arr);
      ((PostMethod) res).setRequestBody(arr);
    }
    client.executeMethod(res);
    final String s = res.getResponseBodyAsString();
    return new SAXBuilder(false).build(new StringReader(s)).getRootElement();
  }

  public static String createRequest(NameValuePair[] pairs) throws IOException, JDOMException {
    final HttpClient client = getClient();
    final HttpMethod res = new PostMethod(POST_URL);
    if (pairs != null) {
      ((PostMethod) res).setRequestBody(pairs);
    }
    client.executeMethod(res);
    return res.getResponseBodyAsString();
  }

  public static String loginRequest(String login, String password) throws IOException {
    final HttpClient client = getClient();
    final HttpMethod res = new PostMethod(LOGIN_URL);
    ((PostMethod) res).setRequestBody(new NameValuePair[]{
        new NameValuePair("api_dev_key", API_KEY),
        new NameValuePair("api_user_name", login),
        new NameValuePair("api_user_password", password)
    });
    client.executeMethod(res);
    return res.getResponseBodyAsString();
  }

  public static NameValuePair[] constructTrendsParameters() {
    return new NameValuePair[]{
        new NameValuePair("api_option", "trends"),
        new NameValuePair("api_dev_key", API_KEY)
    };
  }

  public static NameValuePair[] constructListParameters(final String userKey, final int limit) {
    return new NameValuePair[]{
        new NameValuePair("api_option", "list"),
        new NameValuePair("api_dev_key", API_KEY),
        new NameValuePair("api_user_key", userKey),
        new NameValuePair("api_results_limit", String.valueOf(limit))
    };
  }

  public static NameValuePair[] constructCreateParameters(
      final String option,
      final String text,
      final String userKey,
      final String name,
      final String format,
      final AccessType accessType,
      final ExpireDate date) {
    final List<NameValuePair> list = new ArrayList<NameValuePair>();
    list.add(new NameValuePair("api_option", option));
    list.add(new NameValuePair("api_dev_key", API_KEY));
    list.add(new NameValuePair("api_paste_code", text));
    list.add(new NameValuePair("api_paste_private", accessType.toString()));
    list.add(new NameValuePair("api_paste_expire_date", date.toString()));
    if (userKey != null) {
      list.add(new NameValuePair("api_user_key", userKey));
    }
    if (name != null) {
      list.add(new NameValuePair("api_paste_name", name));
    }
    if (format != null) {
      list.add(new NameValuePair("api_paste_format", format));
    }

    return (NameValuePair[]) list.toArray();
  }

  public static boolean checkCredentials(final String login, final String password) {
    if (login == null && password == null && areCredentialsEmpty()) {
      return false;
    }
    if (login != null && password != null) {
      return testConnection(login, password);
    }
    PastebinSettings settings = PastebinSettings.getInstance();
    return testConnection(settings.getLogin(), settings.getPassword());
  }

  private static boolean testConnection(String login, String password) {
    try {
      String response = loginRequest(login, password);
      if (response.startsWith("Bad")) {
        return false;
      } else {
        PastebinSettings.getInstance().setLoginID(response);
        return true;
      }
    } catch (Exception ignored) {
    }
    return false;
  }

  public static boolean areCredentialsEmpty() {
    PastebinSettings settings = PastebinSettings.getInstance();
    return StringUtil.isEmptyOrSpaces(settings.getLogin()) || StringUtil.isEmptyOrSpaces(settings.getPassword());
  }

  public enum ExpireDate {
    NEVER, TEN_MINUTES, ONE_HOUR, ONE_DAY, ONE_MONTH;

    private static final HashMap<ExpireDate, String> names;

    static {
      names = new HashMap<ExpireDate, String>();
      names.put(NEVER, "N");
      names.put(TEN_MINUTES, "10M");
      names.put(ONE_HOUR, "1H");
      names.put(ONE_DAY, "1D");
      names.put(ONE_MONTH, "1M");
    }

    @Override
    public String toString() {
      return names.get(this);
    }
  }

  public enum AccessType {
    PUBLIC, UNLISTED, PRIVATE;

    private static final HashMap<AccessType, Integer> ids;

    static {
      ids = new HashMap<AccessType, Integer>();
      ids.put(PUBLIC, 0);
      ids.put(UNLISTED, 1);
      ids.put(PRIVATE, 2);
    }

    @Override
    public String toString() {
      return String.valueOf(ids.get(this));
    }
  }

  public static final Map<String, String> languages = new HashMap<String, String>();

  static {
    languages.put("4CS", "4cs");
    languages.put("6502 ACME Cross Assembler", "6502acme");
    languages.put("6502 Kick Assembler", "6502kickass");
    languages.put("6502 TASM/64TASS", "6502tasm");
    languages.put("ABAP", "abap");
    languages.put("ActionScript", "actionscript");
    languages.put("ActionScript 3", "actionscript3");
    languages.put("Ada", "ada");
    languages.put("ALGOL 68", "algol68");
    languages.put("Apache Log", "apache");
    languages.put("AppleScript", "applescript");
    languages.put("APT Sources", "apt_sources");
    languages.put("ASM (NASM)", "asm");
    languages.put("ASP", "asp");
    languages.put("autoconf", "autoconf");
    languages.put("Autohotkey", "autohotkey");
    languages.put("AutoIt", "autoit");
    languages.put("Avisynth", "avisynth");
    languages.put("Awk", "awk");
    languages.put("BASCOM AVR", "bascomavr");
    languages.put("Bash", "bash");
    languages.put("Basic4GL", "basic4gl");
    languages.put("BibTeX", "bibtex");
    languages.put("Blitz Basic", "blitzbasic");
    languages.put("BNF", "bnf");
    languages.put("BOO", "boo");
    languages.put("BrainFuck", "bf");
    languages.put("C", "c");
    languages.put("C for Macs", "c_mac");
    languages.put("C Intermediate Language", "cil");
    languages.put("C#", "csharp");
    languages.put("C++", "cpp");
    languages.put("C++ (with QT extensions)", "cpp-qt");
    languages.put("C: Loadrunner", "c_loadrunner");
    languages.put("CAD DCL", "caddcl");
    languages.put("CAD Lisp", "cadlisp");
    languages.put("CFDG", "cfdg");
    languages.put("ChaiScript", "chaiscript");
    languages.put("Clojure", "clojure");
    languages.put("Clone C", "klonec");
    languages.put("Clone C++", "klonecpp");
    languages.put("CMake", "cmake");
    languages.put("COBOL", "cobol");
    languages.put("CoffeeScript", "coffeescript");
    languages.put("ColdFusion", "cfm");
    languages.put("CSS", "css");
    languages.put("Cuesheet", "cuesheet");
    languages.put("D", "d");
    languages.put("DCS", "dcs");
    languages.put("Delphi", "delphi");
    languages.put("Delphi Prism (Oxygene)", "oxygene");
    languages.put("Diff", "diff");
    languages.put("DIV", "div");
    languages.put("DOS", "dos");
    languages.put("DOT", "dot");
    languages.put("E", "e");
    languages.put("ECMAScript", "ecmascript");
    languages.put("Eiffel", "eiffel");
    languages.put("Email", "email");
    languages.put("EPC", "epc");
    languages.put("Erlang", "erlang");
    languages.put("F#", "fsharp");
    languages.put("Falcon", "falcon");
    languages.put("FO Language", "fo");
    languages.put("Formula One", "f1");
    languages.put("Fortran", "fortran");
    languages.put("FreeBasic", "freebasic");
    languages.put("FreeSWITCH", "freeswitch");
    languages.put("GAMBAS", "gambas");
    languages.put("Game Maker", "gml");
    languages.put("GDB", "gdb");
    languages.put("Genero", "genero");
    languages.put("Genie", "genie");
    languages.put("GetText", "gettext");
    languages.put("Go", "go");
    languages.put("Groovy", "groovy");
    languages.put("GwBasic", "gwbasic");
    languages.put("Haskell", "haskell");
    languages.put("HicEst", "hicest");
    languages.put("HQ9 Plus", "hq9plus");
    languages.put("HTML", "html4strict");
    languages.put("HTML 5", "html5");
    languages.put("Icon", "icon");
    languages.put("IDL", "idl");
    languages.put("INI file", "ini");
    languages.put("Inno Script", "inno");
    languages.put("INTERCAL", "intercal");
    languages.put("IO", "io");
    languages.put("J", "j");
    languages.put("Java", "java");
    languages.put("Java 5", "java5");
    languages.put("JavaScript", "javascript");
    languages.put("jQuery", "jquery");
    languages.put("KiXtart", "kixtart");
    languages.put("Latex", "latex");
    languages.put("Liberty BASIC", "lb");
    languages.put("Linden Scripting", "lsl2");
    languages.put("Lisp", "lisp");
    languages.put("LLVM", "llvm");
    languages.put("Loco Basic", "locobasic");
    languages.put("Logtalk", "logtalk");
    languages.put("LOL Code", "lolcode");
    languages.put("Lotus Formulas", "lotusformulas");
    languages.put("Lotus Script", "lotusscript");
    languages.put("LScript", "lscript");
    languages.put("Lua", "lua");
    languages.put("M68000 Assembler", "m68k");
    languages.put("MagikSF", "magiksf");
    languages.put("Make", "make");
    languages.put("MapBasic", "mapbasic");
    languages.put("MatLab", "matlab");
    languages.put("mIRC", "mirc");
    languages.put("MIX Assembler", "mmix");
    languages.put("Modula 2", "modula2");
    languages.put("Modula 3", "modula3");
    languages.put("Motorola 68000 HiSoft Dev", "68000devpac");
    languages.put("MPASM", "mpasm");
    languages.put("MXML", "mxml");
    languages.put("MySQL", "mysql");
    languages.put("newLISP", "newlisp");
    languages.put("None", "text");
    languages.put("NullSoft Installer", "nsis");
    languages.put("Oberon 2", "oberon2");
    languages.put("Objeck Programming Langua", "objeck");
    languages.put("Objective C", "objc");
    languages.put("OCalm Brief", "ocaml-brief");
    languages.put("OCaml", "ocaml");
    languages.put("OpenBSD PACKET FILTER", "pf");
    languages.put("OpenGL Shading", "glsl");
    languages.put("Openoffice BASIC", "oobas");
    languages.put("Oracle 11", "oracle11");
    languages.put("Oracle 8", "oracle8");
    languages.put("Oz", "oz");
    languages.put("Pascal", "pascal");
    languages.put("PAWN", "pawn");
    languages.put("PCRE", "pcre");
    languages.put("Per", "per");
    languages.put("Perl", "perl");
    languages.put("Perl 6", "perl6");
    languages.put("PHP", "php");
    languages.put("PHP Brief", "php-brief");
    languages.put("Pic 16", "pic16");
    languages.put("Pike", "pike");
    languages.put("Pixel Bender", "pixelbender");
    languages.put("PL/SQL", "plsql");
    languages.put("PostgreSQL", "postgresql");
    languages.put("POV-Ray", "povray");
    languages.put("Power Shell", "powershell");
    languages.put("PowerBuilder", "powerbuilder");
    languages.put("ProFTPd", "proftpd");
    languages.put("Progress", "progress");
    languages.put("Prolog", "prolog");
    languages.put("Properties", "properties");
    languages.put("ProvideX", "providex");
    languages.put("PureBasic", "purebasic");
    languages.put("PyCon", "pycon");
    languages.put("Python", "python");
    languages.put("q/kdb+", "q");
    languages.put("QBasic", "qbasic");
    languages.put("R", "rsplus");
    languages.put("Rails", "rails");
    languages.put("REBOL", "rebol");
    languages.put("REG", "reg");
    languages.put("Robots", "robots");
    languages.put("RPM Spec", "rpmspec");
    languages.put("Ruby", "ruby");
    languages.put("Ruby Gnuplot", "gnuplot");
    languages.put("SAS", "sas");
    languages.put("Scala", "scala");
    languages.put("Scheme", "scheme");
    languages.put("Scilab", "scilab");
    languages.put("SdlBasic", "sdlbasic");
    languages.put("Smalltalk", "smalltalk");
    languages.put("Smarty", "smarty");
    languages.put("SQL", "sql");
    languages.put("SystemVerilog", "systemverilog");
    languages.put("T-SQL", "tsql");
    languages.put("TCL", "tcl");
    languages.put("Tera Term", "teraterm");
    languages.put("thinBasic", "thinbasic");
    languages.put("TypoScript", "typoscript");
    languages.put("Unicon", "unicon");
    languages.put("UnrealScript", "uscript");
    languages.put("Vala", "vala");
    languages.put("VB.NET", "vbnet");
    languages.put("VeriLog", "verilog");
    languages.put("VHDL", "vhdl");
    languages.put("VIM", "vim");
    languages.put("Visual Pro Log", "visualprolog");
    languages.put("VisualBasic", "vb");
    languages.put("VisualFoxPro", "visualfoxpro");
    languages.put("WhiteSpace", "whitespace");
    languages.put("WHOIS", "whois");
    languages.put("Winbatch", "winbatch");
    languages.put("XBasic", "xbasic");
    languages.put("XML", "xml");
    languages.put("Xorg Config", "xorg_conf");
    languages.put("XPP", "xpp");
    languages.put("YAML", "yaml");
    languages.put("Z80 Assembler", "z80");
    languages.put("ZXBasic", "zxbasic");
  }

}
