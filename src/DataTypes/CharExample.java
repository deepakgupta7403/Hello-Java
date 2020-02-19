package DataTypes;

/**The char data type is a single 16-bit Unicode character. A char is a single character.
 * Value: ‘\u0000’ (or null) to ‘\uffff’ 65535
 * Minimum value is '\u0000' (or 0)
 * Maximum value is '\uffff' (or 65,535 inclusive)
 * Char data type is used to store any character
 * Example: char letterA = 'A'**/

/**	Unicode	Hexa
 NCR	Decimal
 NCR	UTF8	Escaped
 Unicode	Description
 �	U+0000	&#x0000;	&#0;	0	\u0000	Null Character
 	U+0001	&#x0001;	&#1;	1	\u0001	Start Of Heading
 	U+0002	&#x0002;	&#2;	2	\u0002	Start Of Text
 	U+0003	&#x0003;	&#3;	3	\u0003	End-of-text Character
 	U+0004	&#x0004;	&#4;	4	\u0004	End-of-transmission Character
 	U+0005	&#x0005;	&#5;	5	\u0005	Enquiry Character
 	U+0006	&#x0006;	&#6;	6	\u0006	Acknowledge Character
 	U+0007	&#x0007;	&#7;	7	\u0007	Bell Character
 	U+0008	&#x0008;	&#8;	8	\u0008	Backspace
 U+0009	&#x0009;	&#9;	9	\u0009	Horizontal Tab
 U+000A	&#x000A;	&#10;	0A	\u000A	Line Feed
 	U+000B	&#x000B;	&#11;	0B	\u000B	Vertical Tab
 U+000C	&#x000C;	&#12;	0C	\u000C	Form Feed

 U+000D	&#x000D;	&#13;	0D	\u000D	Carriage Return
 	U+000E	&#x000E;	&#14;	0E	\u000E	Shift Out
 	U+000F	&#x000F;	&#15;	0F	\u000F	Shift In
 	U+0010	&#x0010;	&#16;	10	\u0010	Data Link Escape
 	U+0011	&#x0011;	&#17;	11	\u0011	Device Control 1
 	U+0012	&#x0012;	&#18;	12	\u0012	Device Control 2
 	U+0013	&#x0013;	&#19;	13	\u0013	Device Control 3
 	U+0014	&#x0014;	&#20;	14	\u0014	Device Control 4
 	U+0015	&#x0015;	&#21;	15	\u0015	Negative-acknowledge Character
 	U+0016	&#x0016;	&#22;	16	\u0016	Synchronous Idle
 	U+0017	&#x0017;	&#23;	17	\u0017	End Of Transmission Block
 	U+0018	&#x0018;	&#24;	18	\u0018	Cancel Character
 	U+0019	&#x0019;	&#25;	19	\u0019	End Of Medium
 	U+001A	&#x001A;	&#26;	1A	\u001A	Substitute Character
 	U+001B	&#x001B;	&#27;	1B	\u001B	Escape Character
 	U+001C	&#x001C;	&#28;	1C	\u001C	File Separator
 	U+001D	&#x001D;	&#29;	1D	\u001D	Group Separator
 	U+001E	&#x001E;	&#30;	1E	\u001E	Record Separator
 	U+001F	&#x001F;	&#31;	1F	\u001F	Unit Separator
 U+0020	&#x0020;	&#32;	20	\u0020	Space
 !	U+0021	&#x0021;	&#33;	21	\u0021	Exclamation Mark
 "	U+0022	&#x0022;	&#34;	22	\u0022	Quotation Mark
 #	U+0023	&#x0023;	&#35;	23	\u0023	Hash Tag
 $	U+0024	&#x0024;	&#36;	24	\u0024	Dollar Sign
 %	U+0025	&#x0025;	&#37;	25	\u0025	Percent Sign
 &	U+0026	&#x0026;	&#38;	26	\u0026	Ampersand
 '	U+0027	&#x0027;	&#39;	27	\u0027	Apostrophe
 (	U+0028	&#x0028;	&#40;	28	\u0028	Left Parenthesis
 )	U+0029	&#x0029;	&#41;	29	\u0029	Right Parenthesis
 *	U+002A	&#x002A;	&#42;	2A	\u002A	Asterisk
 +	U+002B	&#x002B;	&#43;	2B	\u002B	Plus Sign
 ,	U+002C	&#x002C;	&#44;	2C	\u002C	Comma
 -	U+002D	&#x002D;	&#45;	2D	\u002D	Hyphen-minus
 .	U+002E	&#x002E;	&#46;	2E	\u002E	Full Stop
 /	U+002F	&#x002F;	&#47;	2F	\u002F	Slash (solidus)
 0	U+0030	&#x0030;	&#48;	30	\u0030	Digit Zero
 1	U+0031	&#x0031;	&#49;	31	\u0031	Digit One
 2	U+0032	&#x0032;	&#50;	32	\u0032	Digit Two
 3	U+0033	&#x0033;	&#51;	33	\u0033	Digit Three
 4	U+0034	&#x0034;	&#52;	34	\u0034	Digit Four
 5	U+0035	&#x0035;	&#53;	35	\u0035	Digit Five
 6	U+0036	&#x0036;	&#54;	36	\u0036	Digit Six
 7	U+0037	&#x0037;	&#55;	37	\u0037	Digit Seven
 8	U+0038	&#x0038;	&#56;	38	\u0038	Digit Eight
 9	U+0039	&#x0039;	&#57;	39	\u0039	Digit Nine
 :	U+003A	&#x003A;	&#58;	3A	\u003A	Colon
 ;	U+003B	&#x003B;	&#59;	3B	\u003B	Semicolon
 <	U+003C	&#x003C;	&#60;	3C	\u003C	Less-than Sign
 =	U+003D	&#x003D;	&#61;	3D	\u003D	Equal Sign
 >	U+003E	&#x003E;	&#62;	3E	\u003E	Greater-than Sign
 ?	U+003F	&#x003F;	&#63;	3F	\u003F	Question Mark
 @	U+0040	&#x0040;	&#64;	40	\u0040	At Sign
 A	U+0041	&#x0041;	&#65;	41	\u0041	Latin Capital Letter A
 B	U+0042	&#x0042;	&#66;	42	\u0042	Latin Capital Letter B
 C	U+0043	&#x0043;	&#67;	43	\u0043	Latin Capital Letter C
 D	U+0044	&#x0044;	&#68;	44	\u0044	Latin Capital Letter D
 E	U+0045	&#x0045;	&#69;	45	\u0045	Latin Capital Letter E
 F	U+0046	&#x0046;	&#70;	46	\u0046	Latin Capital Letter F
 G	U+0047	&#x0047;	&#71;	47	\u0047	Latin Capital Letter G
 H	U+0048	&#x0048;	&#72;	48	\u0048	Latin Capital Letter H
 I	U+0049	&#x0049;	&#73;	49	\u0049	Latin Capital Letter I
 J	U+004A	&#x004A;	&#74;	4A	\u004A	Latin Capital Letter J
 K	U+004B	&#x004B;	&#75;	4B	\u004B	Latin Capital Letter K
 L	U+004C	&#x004C;	&#76;	4C	\u004C	Latin Capital Letter L
 M	U+004D	&#x004D;	&#77;	4D	\u004D	Latin Capital Letter M
 N	U+004E	&#x004E;	&#78;	4E	\u004E	Latin Capital Letter N
 O	U+004F	&#x004F;	&#79;	4F	\u004F	Latin Capital Letter O
 P	U+0050	&#x0050;	&#80;	50	\u0050	Latin Capital Letter P
 Q	U+0051	&#x0051;	&#81;	51	\u0051	Latin Capital Letter Q
 R	U+0052	&#x0052;	&#82;	52	\u0052	Latin Capital Letter R
 S	U+0053	&#x0053;	&#83;	53	\u0053	Latin Capital Letter S
 T	U+0054	&#x0054;	&#84;	54	\u0054	Latin Capital Letter T
 U	U+0055	&#x0055;	&#85;	55	\u0055	Latin Capital Letter U
 V	U+0056	&#x0056;	&#86;	56	\u0056	Latin Capital Letter V
 W	U+0057	&#x0057;	&#87;	57	\u0057	Latin Capital Letter W
 X	U+0058	&#x0058;	&#88;	58	\u0058	Latin Capital Letter X
 Y	U+0059	&#x0059;	&#89;	59	\u0059	Latin Capital Letter Y
 Z	U+005A	&#x005A;	&#90;	5A	\u005A	Latin Capital Letter Z
 [	U+005B	&#x005B;	&#91;	5B	\u005B	Left Square Bracket
 \	U+005C	&#x005C;	&#92;	5C	\u005C	Backslash
 ]	U+005D	&#x005D;	&#93;	5D	\u005D	Right Square Bracket
 ^	U+005E	&#x005E;	&#94;	5E	\u005E	Circumflex Accent
 _	U+005F	&#x005F;	&#95;	5F	\u005F	Low Line
 `	U+0060	&#x0060;	&#96;	60	\u0060	Grave Accent
 a	U+0061	&#x0061;	&#97;	61	\u0061	Latin Small Letter A
 b	U+0062	&#x0062;	&#98;	62	\u0062	Latin Small Letter B
 c	U+0063	&#x0063;	&#99;	63	\u0063	Latin Small Letter C
 d	U+0064	&#x0064;	&#100;	64	\u0064	Latin Small Letter D
 e	U+0065	&#x0065;	&#101;	65	\u0065	Latin Small Letter E
 f	U+0066	&#x0066;	&#102;	66	\u0066	Latin Small Letter F
 g	U+0067	&#x0067;	&#103;	67	\u0067	Latin Small Letter G
 h	U+0068	&#x0068;	&#104;	68	\u0068	Latin Small Letter H
 i	U+0069	&#x0069;	&#105;	69	\u0069	Latin Small Letter I
 j	U+006A	&#x006A;	&#106;	6A	\u006A	Latin Small Letter J
 k	U+006B	&#x006B;	&#107;	6B	\u006B	Latin Small Letter K
 l	U+006C	&#x006C;	&#108;	6C	\u006C	Latin Small Letter L
 m	U+006D	&#x006D;	&#109;	6D	\u006D	Latin Small Letter M
 n	U+006E	&#x006E;	&#110;	6E	\u006E	Latin Small Letter N
 o	U+006F	&#x006F;	&#111;	6F	\u006F	Latin Small Letter O
 p	U+0070	&#x0070;	&#112;	70	\u0070	Latin Small Letter P
 q	U+0071	&#x0071;	&#113;	71	\u0071	Latin Small Letter Q
 r	U+0072	&#x0072;	&#114;	72	\u0072	Latin Small Letter R
 s	U+0073	&#x0073;	&#115;	73	\u0073	Latin Small Letter S
 t	U+0074	&#x0074;	&#116;	74	\u0074	Latin Small Letter T
 u	U+0075	&#x0075;	&#117;	75	\u0075	Latin Small Letter U
 v	U+0076	&#x0076;	&#118;	76	\u0076	Latin Small Letter V
 w	U+0077	&#x0077;	&#119;	77	\u0077	Latin Small Letter W
 x	U+0078	&#x0078;	&#120;	78	\u0078	Latin Small Letter X
 y	U+0079	&#x0079;	&#121;	79	\u0079	Latin Small Letter Y
 z	U+007A	&#x007A;	&#122;	7A	\u007A	Latin Small Letter Z
 {	U+007B	&#x007B;	&#123;	7B	\u007B	Left Curly Bracket
 |	U+007C	&#x007C;	&#124;	7C	\u007C	Vertical Bar
 }	U+007D	&#x007D;	&#125;	7D	\u007D	Right Curly Bracket
 ~	U+007E	&#x007E;	&#126;	7E	\u007E	Tilde
 	U+007F	&#x007F;	&#127;	7F	\u007F	Delete**/

public class CharExample {

    public static void main(String args[]) {

        // declaring character
        char a = '\u0069';

        // Integer data type is generally
        // used for numeric values
        int i=89;

        // use byte and short if memory is a constraint
        byte b = 4;

        // this will give error as number is
        // larger than byte range
        // byte b1 = 7888888955;

        short s = 56;

        // this will give error as number is
        // larger than short range
        // short s1 = 87878787878;


        // by default fraction value is double in java
        double d = 4.355453532;

        // for float use 'f' as suffix
        float f = 4.7333434f;

        System.out.println("char: " + a);
        System.out.println("integer: " + i);
        System.out.println("byte: " + b);
        System.out.println("short: " + s);
        System.out.println("float: " + f);
        System.out.println("double: " + d);

//        OUTPUT
//        char: i
//        integer: 89
//        byte: 4
//        short: 56
//        float: 4.7333436
//        double: 4.355453532

    }
}
