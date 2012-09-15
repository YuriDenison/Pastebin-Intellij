package com.pastebin.jetbrains;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuri Denison
 * @date 15.09.12
 */
public class Paste {
  private String text;
  private String key;
  private String name;
  private String language;
  private ExpireDate expireDate;
  private AccessType accessType;

  private long date;
  private int hits;
  private String url;

  public Paste(String text, String name, String language, ExpireDate expireDate, AccessType accessType) {
    this.text = text;
    this.name = name;
    this.language = language;
    this.expireDate = expireDate;
    this.accessType = accessType;
  }

  public Paste(String name, String language, ExpireDate expireDate, AccessType accessType, String key, long date, int hits, String url) {
    this.text = null;
    this.key = key;
    this.name = name;
    this.language = language;
    this.expireDate = expireDate;
    this.accessType = accessType;
    this.date = date;
    this.hits = hits;
    this.url = url;
  }

  public String getKey() {
    return key;
  }

  public AccessType getAccessType() {
    return accessType;
  }

  public String getText() {
    if (text == null && key != null) {
      text = PastebinUtil.getRawPasteText(key);
    }
    return text;
  }

  public ExpireDate getExpireDate() {
    return expireDate;
  }

  public String getName() {
    return name;
  }

  public String getLanguage() {
    return language;
  }

  public enum ExpireDate {
    NEVER("Never", "N"),
    TEN_MINUTES("10 Minutes", "10M"),
    ONE_HOUR("1 Hour", "1H"),
    ONE_DAY("1 Day", "1D"),
    ONE_MONTH("1 Month", "1M");

    private String printName;
    private String pastebinCode;

    private ExpireDate(final String printName, final String pastebinCode) {
      this.printName = printName;
      this.pastebinCode = pastebinCode;
    }

    @Override
    public String toString() {
      return printName;
    }

    public String getPastebinCode() {
      return pastebinCode;
    }

    public static ExpireDate getExpireDate(final String code) {
      for (ExpireDate date : ExpireDate.values()) {
        if (code.equals(date.pastebinCode)) {
          return date;
        }
      }
      return null;
    }
  }

  public enum AccessType {
    PUBLIC(0), UNLISTED(1), PRIVATE(2);
    private int pastebinCode;

    private AccessType(int code) {
      pastebinCode = code;
    }

    @Override
    public String toString() {
      return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public String getPastebinCode() {
      return String.valueOf(pastebinCode);
    }

    public static AccessType getAccessType(final int code) {
      for (AccessType type : AccessType.values()) {
        if (code == type.pastebinCode) {
          return type;
        }
      }
      return null;
    }
  }

  public static final Map<String, String> languages = new HashMap<String, String>();

  static {
//    languages.put("4CS", "4cs");
//    languages.put("6502 ACME Cross Assembler", "6502acme");
//    languages.put("6502 Kick Assembler", "6502kickass");
//    languages.put("6502 TASM/64TASS", "6502tasm");
//    languages.put("ABAP", "abap");
    languages.put("ActionScript", "actionscript");
//    languages.put("ActionScript 3", "actionscript3");
//    languages.put("Ada", "ada");
//    languages.put("ALGOL 68", "algol68");
//    languages.put("Apache Log", "apache");
//    languages.put("AppleScript", "applescript");
//    languages.put("APT Sources", "apt_sources");
//    languages.put("ASM (NASM)", "asm");
//    languages.put("ASP", "asp");
//    languages.put("autoconf", "autoconf");
//    languages.put("Autohotkey", "autohotkey");
//    languages.put("AutoIt", "autoit");
//    languages.put("Avisynth", "avisynth");
//    languages.put("Awk", "awk");
//    languages.put("BASCOM AVR", "bascomavr");
    languages.put("Bash", "bash");
//    languages.put("Basic4GL", "basic4gl");
//    languages.put("BibTeX", "bibtex");
//    languages.put("Blitz Basic", "blitzbasic");
//    languages.put("BNF", "bnf");
//    languages.put("BOO", "boo");
    languages.put("BrainFuck", "bf");
    languages.put("C", "c");
//    languages.put("C for Macs", "c_mac");
//    languages.put("C Intermediate Language", "cil");
    languages.put("C#", "csharp");
    languages.put("C++", "cpp");
//    languages.put("C++ (with QT extensions)", "cpp-qt");
//    languages.put("C: Loadrunner", "c_loadrunner");
//    languages.put("CAD DCL", "caddcl");
//    languages.put("CAD Lisp", "cadlisp");
//    languages.put("CFDG", "cfdg");
//    languages.put("ChaiScript", "chaiscript");
    languages.put("Clojure", "clojure");
//    languages.put("Clone C", "klonec");
//    languages.put("Clone C++", "klonecpp");
//    languages.put("CMake", "cmake");
//    languages.put("COBOL", "cobol");
    languages.put("CoffeeScript", "coffeescript");
//    languages.put("ColdFusion", "cfm");
    languages.put("CSS", "css");
//    languages.put("Cuesheet", "cuesheet");
//    languages.put("D", "d");
//    languages.put("DCS", "dcs");
//    languages.put("Delphi", "delphi");
//    languages.put("Delphi Prism (Oxygene)", "oxygene");
//    languages.put("Diff", "diff");
//    languages.put("DIV", "div");
//    languages.put("DOS", "dos");
//    languages.put("DOT", "dot");
//    languages.put("E", "e");
    languages.put("ECMAScript", "ecmascript");
//    languages.put("Eiffel", "eiffel");
//    languages.put("Email", "email");
//    languages.put("EPC", "epc");
//    languages.put("Erlang", "erlang");
//    languages.put("F#", "fsharp");
//    languages.put("Falcon", "falcon");
//    languages.put("FO Language", "fo");
//    languages.put("Formula One", "f1");
//    languages.put("Fortran", "fortran");
//    languages.put("FreeBasic", "freebasic");
//    languages.put("FreeSWITCH", "freeswitch");
//    languages.put("GAMBAS", "gambas");
//    languages.put("Game Maker", "gml");
//    languages.put("GDB", "gdb");
//    languages.put("Genero", "genero");
//    languages.put("Genie", "genie");
//    languages.put("GetText", "gettext");
    languages.put("Go", "go");
    languages.put("Groovy", "groovy");
//    languages.put("GwBasic", "gwbasic");
    languages.put("Haskell", "haskell");
//    languages.put("HicEst", "hicest");
//    languages.put("HQ9 Plus", "hq9plus");
    languages.put("HTML", "html4strict");
//    languages.put("HTML 5", "html5");
//    languages.put("Icon", "icon");
//    languages.put("IDL", "idl");
    languages.put("INI file", "ini");
//    languages.put("Inno Script", "inno");
//    languages.put("INTERCAL", "intercal");
//    languages.put("IO", "io");
//    languages.put("J", "j");
    languages.put("Java", "java");
//    languages.put("Java 5", "java5");
    languages.put("JavaScript", "javascript");
    languages.put("jQuery", "jquery");
//    languages.put("KiXtart", "kixtart");
//    languages.put("Latex", "latex");
//    languages.put("Liberty BASIC", "lb");
//    languages.put("Linden Scripting", "lsl2");
//    languages.put("Lisp", "lisp");
//    languages.put("LLVM", "llvm");
//    languages.put("Loco Basic", "locobasic");
//    languages.put("Logtalk", "logtalk");
//    languages.put("LOL Code", "lolcode");
//    languages.put("Lotus Formulas", "lotusformulas");
//    languages.put("Lotus Script", "lotusscript");
//    languages.put("LScript", "lscript");
//    languages.put("Lua", "lua");
//    languages.put("M68000 Assembler", "m68k");
//    languages.put("MagikSF", "magiksf");
    languages.put("Make", "make");
//    languages.put("MapBasic", "mapbasic");
//    languages.put("MatLab", "matlab");
//    languages.put("mIRC", "mirc");
//    languages.put("MIX Assembler", "mmix");
//    languages.put("Modula 2", "modula2");
//    languages.put("Modula 3", "modula3");
//    languages.put("Motorola 68000 HiSoft Dev", "68000devpac");
//    languages.put("MPASM", "mpasm");
//    languages.put("MXML", "mxml");
    languages.put("MySQL", "mysql");
//    languages.put("newLISP", "newlisp");
    languages.put("None", "text");
//    languages.put("NullSoft Installer", "nsis");
//    languages.put("Oberon 2", "oberon2");
//    languages.put("Objeck Programming Langua", "objeck");
    languages.put("Objective C", "objc");
//    languages.put("OCalm Brief", "ocaml-brief");
//    languages.put("OCaml", "ocaml");
//    languages.put("OpenBSD PACKET FILTER", "pf");
//    languages.put("OpenGL Shading", "glsl");
//    languages.put("Openoffice BASIC", "oobas");
//    languages.put("Oracle 11", "oracle11");
//    languages.put("Oracle 8", "oracle8");
//    languages.put("Oz", "oz");
    languages.put("Pascal", "pascal");
//    languages.put("PAWN", "pawn");
//    languages.put("PCRE", "pcre");
//    languages.put("Per", "per");
    languages.put("Perl", "perl");
//    languages.put("Perl 6", "perl6");
    languages.put("PHP", "php");
//    languages.put("PHP Brief", "php-brief");
//    languages.put("Pic 16", "pic16");
//    languages.put("Pike", "pike");
//    languages.put("Pixel Bender", "pixelbender");
//    languages.put("PL/SQL", "plsql");
//    languages.put("PostgreSQL", "postgresql");
//    languages.put("POV-Ray", "povray");
//    languages.put("Power Shell", "powershell");
//    languages.put("PowerBuilder", "powerbuilder");
//    languages.put("ProFTPd", "proftpd");
//    languages.put("Progress", "progress");
//    languages.put("Prolog", "prolog");
//    languages.put("Properties", "properties");
//    languages.put("ProvideX", "providex");
//    languages.put("PureBasic", "purebasic");
//    languages.put("PyCon", "pycon");
    languages.put("Python", "python");
//    languages.put("q/kdb+", "q");
//    languages.put("QBasic", "qbasic");
//    languages.put("R", "rsplus");
    languages.put("Rails", "rails");
//    languages.put("REBOL", "rebol");
//    languages.put("REG", "reg");
//    languages.put("Robots", "robots");
//    languages.put("RPM Spec", "rpmspec");
    languages.put("Ruby", "ruby");
//    languages.put("Ruby Gnuplot", "gnuplot");
//    languages.put("SAS", "sas");
    languages.put("Scala", "scala");
//    languages.put("Scheme", "scheme");
//    languages.put("Scilab", "scilab");
//    languages.put("SdlBasic", "sdlbasic");
    languages.put("Smalltalk", "smalltalk");
//    languages.put("Smarty", "smarty");
    languages.put("SQL", "sql");
//    languages.put("SystemVerilog", "systemverilog");
//    languages.put("T-SQL", "tsql");
//    languages.put("TCL", "tcl");
//    languages.put("Tera Term", "teraterm");
//    languages.put("thinBasic", "thinbasic");
//    languages.put("TypoScript", "typoscript");
//    languages.put("Unicon", "unicon");
//    languages.put("UnrealScript", "uscript");
//    languages.put("Vala", "vala");
//    languages.put("VB.NET", "vbnet");
//    languages.put("VeriLog", "verilog");
//    languages.put("VHDL", "vhdl");
    languages.put("VIM", "vim");
//    languages.put("Visual Pro Log", "visualprolog");
//    languages.put("VisualBasic", "vb");
//    languages.put("VisualFoxPro", "visualfoxpro");
//    languages.put("WhiteSpace", "whitespace");
//    languages.put("WHOIS", "whois");
//    languages.put("Winbatch", "winbatch");
//    languages.put("XBasic", "xbasic");
    languages.put("XML", "xml");
//    languages.put("Xorg Config", "xorg_conf");
//    languages.put("XPP", "xpp");
    languages.put("YAML", "yaml");
//    languages.put("Z80 Assembler", "z80");
//    languages.put("ZXBasic", "zxbasic");
  }
}
