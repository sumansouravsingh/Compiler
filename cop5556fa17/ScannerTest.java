/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "  //suman\n  ";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	
	 */
	@Test
	public void testCommentsAndWhiteSpace() throws LexicalException {
		String input = "123abv//comments\n\t$_a x";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.INTEGER_LITERAL, 0,3, 1, 1);
			checkNext(scanner, Kind.IDENTIFIER, 3,3, 1, 4);
			checkNext(scanner, Kind.IDENTIFIER, 18,3, 2, 2);
			checkNext(scanner, Kind.KW_x, 22,1, 2, 6);
			
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void testString() throws LexicalException {
		String input="";
		input = "\" gr\\teetings  \"abcd \"\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, STRING_LITERAL, 0, 16, 1, 1);
		checkNext(scanner, IDENTIFIER, 16, 4, 1, 17);
		checkNext(scanner, STRING_LITERAL, 21, 2, 1, 22);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testStringLiteralInvalid() throws LexicalException {
		String input = "\"sum\nan\"";
		try{
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		}
		catch(LexicalException l){
			show(l);
			assertEquals(4, l.getPos());
		}
	}
	
	@Test
	public void testStringLiteralInvalid1() throws LexicalException {
		try {
			String input="\"abc\\gk\" ";
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner); 
		} catch (LexicalException e) {
			show(e);
			assertEquals(5, e.getPos());
		}
	}
	
	@Test
	public void testStringLiteralBackSlash() throws LexicalException {
		String input = "";
		try{
			input = "\"\\\\\\\\\\\\\\\\\\\\\"a";
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.STRING_LITERAL, 0, 12, 1, 1);
			checkNext(scanner, KW_a,12,1,1,13);
			checkNextIsEOF(scanner);
		}
		catch(LexicalException e){
			show(e);
			assertEquals(4, e.getPos());
		}
	}
	
	@Test
	public void testStringLiteralEscape() throws LexicalException {
		String input = " \" \\\" \" ";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
		} catch (LexicalException e) {
			show(e);
		}
	}


	@Test
	public void testStringLiteralEscapeInvalid() throws LexicalException {
		String input = "\"\\\b\"";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.STRING_LITERAL, 0,4, 1, 1);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
		}
	}


	@Test
	public void testSemi() throws LexicalException {
		String input="";
		input = ";;\n;//Suman/\r\n;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 14, 1, 3, 1);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testOp() throws LexicalException {
		String input = ";;\r\n[](),/ //";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, LSQUARE, 4, 1, 2, 1);
		checkNext(scanner, RSQUARE, 5, 1, 2, 2);
		checkNext(scanner, LPAREN, 6, 1, 2, 3);
		checkNext(scanner, RPAREN, 7, 1, 2, 4);
		checkNext(scanner, COMMA, 8, 1, 2, 5);
		checkNext(scanner, OP_DIV, 9, 1, 2, 6);
		checkNextIsEOF(scanner);
	}
	@Test
	public void testOp1() throws LexicalException {
		String input = "[()]= ===";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LSQUARE, 0, 1, 1, 1);
		checkNext(scanner, LPAREN, 1, 1, 1, 2);
		checkNext(scanner, RPAREN, 2, 1,1, 3);
		checkNext(scanner, RSQUARE, 3, 1, 1, 4);
		checkNext(scanner, OP_ASSIGN, 4, 1, 1, 5);
		checkNext(scanner, OP_EQ, 6, 2, 1, 7);
		checkNext(scanner, OP_ASSIGN, 8, 1, 1, 9);
		checkNextIsEOF(scanner);
		
	}
	
	@Test
	public void testOp3() throws LexicalException {
		String input = "+*,;==:?&@=";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,Kind.OP_PLUS , 0, 1, 1, 1);
		checkNext(scanner,Kind.OP_TIMES ,1, 1, 1,2);
		checkNext(scanner,Kind.COMMA , 2, 1, 1,3);
		checkNext(scanner,Kind.SEMI , 3, 1, 1,4);
		checkNext(scanner,Kind.OP_EQ , 4, 2, 1,5);
		checkNext(scanner,Kind.OP_COLON , 6, 1, 1,7);
		checkNext(scanner,Kind.OP_Q , 7, 1, 1,8);
		checkNext(scanner,Kind.OP_AND , 8, 1, 1,9);
		checkNext(scanner,Kind.OP_AT , 9, 1, 1,10);
		checkNext(scanner,Kind.OP_ASSIGN , 10, 1, 1,11);
		checkNextIsEOF(scanner);
	}

	@Test
	public void testOp4() throws LexicalException {
		String input = "\t==!=<=>=**-><-";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,Kind.OP_EQ , 1, 2, 1, 2);
		checkNext(scanner,Kind.OP_NEQ ,3, 2, 1,4);
		checkNext(scanner,Kind.OP_LE , 5, 2, 1,6);
		checkNext(scanner,Kind.OP_GE , 7, 2, 1,8);
		checkNext(scanner,Kind.OP_POWER , 9, 2, 1,10);
		checkNext(scanner,Kind.OP_RARROW , 11, 2, 1,12);
		checkNext(scanner,Kind.OP_LARROW , 13, 2, 1,14);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testIntegerLit() throws LexicalException {
		String input = "0345\n\n 123456";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.INTEGER_LITERAL, 0, 1, 1, 1);
			checkNext(scanner, Kind.INTEGER_LITERAL, 1, 3, 1, 2);
			checkNext(scanner, Kind.INTEGER_LITERAL, 7, 6, 3, 2);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
		}
	}
	
	@Test
	public void testInvalidInteger() throws LexicalException {
		String input = "0\n1234566799999999999999999999999999999";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner); 
		} catch (LexicalException e) {
			show(e);
			assertEquals(2,e.getPos());
			
		}
	}
	
	@Test
	public void testIdentifier() throws LexicalException {
		String input = "abcdef ";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner); 
			checkNext(scanner, Kind.IDENTIFIER, 0, 6, 1, 1);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
		}
	}
	
	@Test
	public void testIdentifierAlphaNumeric() throws LexicalException {
		String input = "abc123 456def";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.IDENTIFIER, 0, 6, 1, 1);
			checkNext(scanner, Kind.INTEGER_LITERAL, 7, 3, 1, 8);
			checkNext(scanner, Kind.IDENTIFIER, 10, 3, 1, 11);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testIdentifierAlphaNumeric1() throws LexicalException {
		String input = "123abc";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.INTEGER_LITERAL, 0, 3, 1, 1);
			checkNext(scanner, Kind.IDENTIFIER, 3, 3, 1, 4);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
		}
	}
	
	@Test
	public void testIdentifierAlphaNumeric2() throws LexicalException {
		String input = "0\n123abc000";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.INTEGER_LITERAL, 0, 1, 1, 1);
			checkNext(scanner, Kind.INTEGER_LITERAL, 2, 3, 2, 1);
			checkNext(scanner, Kind.IDENTIFIER, 5, 6, 2, 4);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testKeyword() throws LexicalException {
		String input = "DEF_X+";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.KW_DEF_X, 0, 5, 1, 1);
			checkNext(scanner, Kind.OP_PLUS, 5, 1, 1, 6);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testBooleanLiteral() throws LexicalException {
		String input = "\t\r\n sin == false";
		System.out.println(input);
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner); 
			checkNext(scanner, Kind.KW_sin, 4,3, 2, 2);
			checkNext(scanner, Kind.OP_EQ, 8,2, 2, 6);
			checkNext(scanner, Kind.BOOLEAN_LITERAL, 11,5, 2, 9);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	
		
	//Test of op, \n, comment digit
	@Test
	public void testMix1() throws LexicalException {
		String input="";
		input = ";;\n;//Suman/\r\n12345;/!!==\n!=";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, INTEGER_LITERAL, 14, 5, 3, 1);
		checkNext(scanner, SEMI, 19, 1, 3, 6);
		checkNext(scanner, OP_DIV, 20, 1, 3, 7);
		checkNext(scanner, OP_EXCL, 21, 1, 3, 8);
		checkNext(scanner, OP_NEQ, 22, 2, 3, 9);
		checkNext(scanner, OP_ASSIGN, 24, 1, 3, 11);
		checkNext(scanner, OP_NEQ, 26, 2, 4, 1);
		checkNextIsEOF(scanner);
	}

	//Mix of comment \n, identifer string keyword
	@Test
	public void testMix2() throws LexicalException {
		String input="";
		input = "//abcd\n\"greeting@\\n\"  false sinx xsin sin";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,STRING_LITERAL,7,13,2,1);
		checkNext(scanner, BOOLEAN_LITERAL, 22, 5, 2, 16);
		checkNext(scanner, IDENTIFIER, 28, 4, 2, 22);
		checkNext(scanner, IDENTIFIER, 33, 4, 2, 27);
		checkNext(scanner, KW_sin, 38, 3, 2, 32);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testMix3() throws LexicalException {
		String input = ";;\n;12345;\n<= == = >= <- \n!==!*** ++ + @@ ? ::-/->";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, INTEGER_LITERAL, 4, 5, 2, 2);
		checkNext(scanner, SEMI, 9, 1, 2, 7);
		checkNext(scanner, OP_LE, 11, 2, 3, 1);
		checkNext(scanner, OP_EQ, 14, 2, 3, 4);
		checkNext(scanner, OP_ASSIGN, 17, 1, 3, 7);
		checkNext(scanner, OP_GE, 19, 2, 3, 9);
		checkNext(scanner, Kind.OP_LARROW, 22, 2, 3, 12);
		checkNext(scanner, OP_NEQ, 26, 2, 4, 1);
		checkNext(scanner, OP_ASSIGN, 28, 1, 4, 3);
		checkNext(scanner, OP_EXCL, 29, 1, 4, 4);
		checkNext(scanner, OP_POWER, 30, 2, 4, 5);
		checkNext(scanner, OP_TIMES, 32, 1, 4, 7);
		checkNext(scanner, OP_PLUS, 34, 1, 4, 9);
		checkNext(scanner, OP_PLUS, 35, 1, 4, 10);
		checkNext(scanner, OP_PLUS, 37, 1, 4, 12);
		checkNext(scanner, OP_AT, 39, 1, 4, 14);
		checkNext(scanner, OP_AT, 40, 1, 4, 15);
		checkNext(scanner, OP_Q, 42, 1, 4, 17);
		checkNext(scanner, OP_COLON, 44, 1, 4, 19);
		checkNext(scanner, OP_COLON, 45, 1, 4, 20);
		checkNext(scanner, OP_MINUS, 46, 1, 4, 21);
		checkNext(scanner, OP_DIV, 47, 1, 4, 22);
		checkNext(scanner, OP_RARROW, 48, 2, 4, 23);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testMix4() throws LexicalException {
		String input = ";;\n;//Suman/\n12345;/!!==\n!=";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, INTEGER_LITERAL, 13, 5, 3, 1);
		checkNext(scanner, SEMI, 18, 1, 3, 6);
		checkNext(scanner, OP_DIV, 19, 1, 3, 7);
		checkNext(scanner, OP_EXCL, 20, 1, 3, 8);
		checkNext(scanner, OP_NEQ, 21, 2, 3, 9);
		checkNext(scanner, OP_ASSIGN, 23, 1, 3, 11);
		checkNext(scanner, Kind.OP_NEQ, 25, 2, 4, 1);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testMix5() throws LexicalException {
		String input = "IDENTIFIER , INTEGER_LITERAL , BOOLEAN_LITERAL , STRING_LITERAL,  x ,  X,  y,  Y, KW_r r , KW_R R , KW_a a ,KW_A A , KW_Z Z , KW_DEF_X DEF_X , KW_DEF_Y DEF_Y , KW_SCREEN SCREEN , KW_cart_x cart_x ,KW_cart_y cart_y , KW_polar_a polar_a , KW_polar_r polar_r , KW_abs abs , KW_sin sin , KW_cos cos , KW_atan atan , KW_log log , KW_image image ,  KW_int int , KW_boolean boolean , KW_url url , KW_file file , OP_ASSIGN = , OP_GT > , OP_LT < , OP_EXCL ! , OP_Q ? , OP_COLON : , OP_EQ == , OP_NEQ != , OP_GE >= , OP_LE <= ,	OP_AND & , OP_OR | , OP_PLUS + , OP_MINUS - , OP_TIMES * , OP_DIV / , OP_MOD %, OP_POWER ** , OP_AT @, OP_RARROW -> , OP_LARROW <- , LPAREN ( , RPAREN ) , LSQUARE [ , RSQUARE ] , SEMI ; , COMMA , ";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,IDENTIFIER,0,10,1,1);
		checkNext(scanner,COMMA,11,1,1,12);
		checkNext(scanner,IDENTIFIER,13,15,1,14);
		checkNext(scanner,COMMA,29,1,1,30);
		checkNext(scanner,IDENTIFIER,31,15,1,32);
		checkNext(scanner,COMMA,47,1,1,48);
		checkNext(scanner,IDENTIFIER,49,14,1,50);
		checkNext(scanner,COMMA,63,1,1,64);
		checkNext(scanner,KW_x,66,1,1,67);
		checkNext(scanner,COMMA,68,1,1,69);
		checkNext(scanner,KW_X,71,1,1,72);
		checkNext(scanner,COMMA,72,1,1,73);
		checkNext(scanner,KW_y,75,1,1,76);
		checkNext(scanner,COMMA,76,1,1,77);
		checkNext(scanner,KW_Y,79,1,1,80);
		checkNext(scanner,COMMA,80,1,1,81);
		checkNext(scanner,IDENTIFIER,82,4,1,83);
		checkNext(scanner,KW_r,87,1,1,88);
		checkNext(scanner,COMMA,89,1,1,90);
		checkNext(scanner,IDENTIFIER,91,4,1,92);
		checkNext(scanner,KW_R,96,1,1,97);
		checkNext(scanner,COMMA,98,1,1,99);
		checkNext(scanner,IDENTIFIER,100,4,1,101);
		checkNext(scanner,KW_a,105,1,1,106);
		checkNext(scanner,COMMA,107,1,1,108);
		checkNext(scanner,IDENTIFIER,108,4,1,109);
		checkNext(scanner,KW_A,113,1,1,114);
		checkNext(scanner,COMMA,115,1,1,116);
		checkNext(scanner,IDENTIFIER,117,4,1,118);
		checkNext(scanner,KW_Z,122,1,1,123);
		checkNext(scanner,COMMA,124,1,1,125);
		checkNext(scanner,IDENTIFIER,126,8,1,127);
		checkNext(scanner,KW_DEF_X,135,5,1,136);
		checkNext(scanner,COMMA,141,1,1,142);
		checkNext(scanner,IDENTIFIER,143,8,1,144);
		checkNext(scanner,KW_DEF_Y,152,5,1,153);
		checkNext(scanner,COMMA,158,1,1,159);
		checkNext(scanner,IDENTIFIER,160,9,1,161);
		checkNext(scanner,KW_SCREEN,170,6,1,171);
		checkNext(scanner,COMMA,177,1,1,178);
		checkNext(scanner,IDENTIFIER,179,9,1,180);
		checkNext(scanner,KW_cart_x,189,6,1,190);
		checkNext(scanner,COMMA,196,1,1,197);
		checkNext(scanner,IDENTIFIER,197,9,1,198);
		checkNext(scanner,KW_cart_y,207,6,1,208);
		checkNext(scanner,COMMA,214,1,1,215);
		checkNext(scanner,IDENTIFIER,216,10,1,217);
		checkNext(scanner,KW_polar_a,227,7,1,228);
		checkNext(scanner,COMMA,235,1,1,236);
		checkNext(scanner,IDENTIFIER,237,10,1,238);
		checkNext(scanner,KW_polar_r,248,7,1,249);
		checkNext(scanner,COMMA,256,1,1,257);
		checkNext(scanner,IDENTIFIER,258,6,1,259);
		checkNext(scanner,KW_abs,265,3,1,266);
		checkNext(scanner,COMMA,269,1,1,270);
		checkNext(scanner,IDENTIFIER,271,6,1,272);
		checkNext(scanner,KW_sin,278,3,1,279);
		checkNext(scanner,COMMA,282,1,1,283);
		checkNext(scanner,IDENTIFIER,284,6,1,285);
		checkNext(scanner,KW_cos,291,3,1,292);
		checkNext(scanner,COMMA,295,1,1,296);
		checkNext(scanner,IDENTIFIER,297,7,1,298);
		checkNext(scanner,KW_atan,305,4,1,306);
		checkNext(scanner,COMMA,310,1,1,311);
		checkNext(scanner,IDENTIFIER,312,6,1,313);
		checkNext(scanner,KW_log,319,3,1,320);
		checkNext(scanner,COMMA,323,1,1,324);
		checkNext(scanner,IDENTIFIER,325,8,1,326);
		checkNext(scanner,KW_image,334,5,1,335);
		checkNext(scanner,COMMA,340,1,1,341);
		checkNext(scanner,IDENTIFIER,343,6,1,344);
		checkNext(scanner,KW_int,350,3,1,351);
		checkNext(scanner,COMMA,354,1,1,355);
		checkNext(scanner,IDENTIFIER,356,10,1,357);
		checkNext(scanner,KW_boolean,367,7,1,368);
		checkNext(scanner,COMMA,375,1,1,376);
		checkNext(scanner,IDENTIFIER,377,6,1,378);
		checkNext(scanner,KW_url,384,3,1,385);
		checkNext(scanner,COMMA,388,1,1,389);
		checkNext(scanner,IDENTIFIER,390,7,1,391);
		checkNext(scanner,KW_file,398,4,1,399);
		checkNext(scanner,COMMA,403,1,1,404);
		checkNext(scanner,IDENTIFIER,405,9,1,406);
		checkNext(scanner,OP_ASSIGN,415,1,1,416);
		checkNext(scanner,COMMA,417,1,1,418);
		checkNext(scanner,IDENTIFIER,419,5,1,420);
		checkNext(scanner,OP_GT,425,1,1,426);
		checkNext(scanner,COMMA,427,1,1,428);
		checkNext(scanner,IDENTIFIER,429,5,1,430);
		checkNext(scanner,OP_LT,435,1,1,436);
		checkNext(scanner,COMMA,437,1,1,438);
		checkNext(scanner,IDENTIFIER,439,7,1,440);
		checkNext(scanner,OP_EXCL,447,1,1,448);
		checkNext(scanner,COMMA,449,1,1,450);
		checkNext(scanner,IDENTIFIER,451,4,1,452);
		checkNext(scanner,OP_Q,456,1,1,457);
		checkNext(scanner,COMMA,458,1,1,459);
		checkNext(scanner,IDENTIFIER,460,8,1,461);
		checkNext(scanner,OP_COLON,469,1,1,470);
		checkNext(scanner,COMMA,471,1,1,472);
		checkNext(scanner,IDENTIFIER,473,5,1,474);
		checkNext(scanner,OP_EQ,479,2,1,480);
		checkNext(scanner,COMMA,482,1,1,483);
		checkNext(scanner,IDENTIFIER,484,6,1,485);
		checkNext(scanner,OP_NEQ,491,2,1,492);
		checkNext(scanner,COMMA,494,1,1,495);
		checkNext(scanner,IDENTIFIER,496,5,1,497);
		checkNext(scanner,OP_GE,502,2,1,503);
		checkNext(scanner,COMMA,505,1,1,506);
		checkNext(scanner,IDENTIFIER,507,5,1,508);
		checkNext(scanner,OP_LE,513,2,1,514);
		checkNext(scanner,COMMA,516,1,1,517);
		checkNext(scanner,IDENTIFIER,518,6,1,519);
		checkNext(scanner,OP_AND,525,1,1,526);
		checkNext(scanner,COMMA,527,1,1,528);
		checkNext(scanner,IDENTIFIER,529,5,1,530);
		checkNext(scanner,OP_OR,535,1,1,536);
		checkNext(scanner,COMMA,537,1,1,538);
		checkNext(scanner,IDENTIFIER,539,7,1,540);
		checkNext(scanner,OP_PLUS,547,1,1,548);
		checkNext(scanner,COMMA,549,1,1,550);
		checkNext(scanner,IDENTIFIER,551,8,1,552);
		checkNext(scanner,OP_MINUS,560,1,1,561);
		checkNext(scanner,COMMA,562,1,1,563);
		checkNext(scanner,IDENTIFIER,564,8,1,565);
		checkNext(scanner,OP_TIMES,573,1,1,574);
		checkNext(scanner,COMMA,575,1,1,576);
		checkNext(scanner,IDENTIFIER,577,6,1,578);
		checkNext(scanner,OP_DIV,584,1,1,585);
		checkNext(scanner,COMMA,586,1,1,587);
		checkNext(scanner,IDENTIFIER,588,6,1,589);
		checkNext(scanner,OP_MOD,595,1,1,596);
		checkNext(scanner,COMMA,596,1,1,597);
		checkNext(scanner,IDENTIFIER,598,8,1,599);
		checkNext(scanner,OP_POWER,607,2,1,608);
		checkNext(scanner,COMMA,610,1,1,611);
		checkNext(scanner,IDENTIFIER,612,5,1,613);
		checkNext(scanner,OP_AT,618,1,1,619);
		checkNext(scanner,COMMA,619,1,1,620);
		checkNext(scanner,IDENTIFIER,621,9,1,622);
		checkNext(scanner,OP_RARROW,631,2,1,632);
		checkNext(scanner,COMMA,634,1,1,635);
		checkNext(scanner,IDENTIFIER,636,9,1,637);
		checkNext(scanner,OP_LARROW,646,2,1,647);
		checkNext(scanner,COMMA,649,1,1,650);
		checkNext(scanner,IDENTIFIER,651,6,1,652);
		checkNext(scanner,LPAREN,658,1,1,659);
		checkNext(scanner,COMMA,660,1,1,661);
		checkNext(scanner,IDENTIFIER,662,6,1,663);
		checkNext(scanner,RPAREN,669,1,1,670);
		checkNext(scanner,COMMA,671,1,1,672);
		checkNext(scanner,IDENTIFIER,673,7,1,674);
		checkNext(scanner,LSQUARE,681,1,1,682);
		checkNext(scanner,COMMA,683,1,1,684);
		checkNext(scanner,IDENTIFIER,685,7,1,686);
		checkNext(scanner,RSQUARE,693,1,1,694);
		checkNext(scanner,COMMA,695,1,1,696);
		checkNext(scanner,IDENTIFIER,697,4,1,698);
		checkNext(scanner,SEMI,702,1,1,703);
		checkNext(scanner,COMMA,704,1,1,705);
		checkNext(scanner,IDENTIFIER,706,5,1,707);
		checkNext(scanner,COMMA,712,1,1,713);
		checkNextIsEOF(scanner);

	}
	
	@Test
	public void testInvalidCornerCases1() throws LexicalException {
		String input = "//Suman/\n\"\\\\\"\"";
		try{
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, STRING_LITERAL, 9, 4, 3, 1);
			checkNextIsEOF(scanner);
		}
		catch(LexicalException e)
		{
			show(e);
			assertEquals(14, e.getPos());
		}
	}

	@Test
	public void testInvalidCornerCases2() throws LexicalException {
		String input = "//Suman/\n\\n\"\\\\\"\"";
		try{
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNextIsEOF(scanner);
		}
		catch(LexicalException e)
		{
			show(e);
			assertEquals(9,e.getPos());
		}
	}
	
	
	@Test
	public void testValidCornerCases() throws LexicalException {
		String input = "//Suman/\n\"\\\"\"";
		try{
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, STRING_LITERAL, 9, 4, 2, 1);
			checkNextIsEOF(scanner);
		}
		catch(LexicalException e)
		{
			show(e);
		}
	}
	
	
	@Test
	public void testBiswajitCase1() throws LexicalException {
		String input = " \r\n \r\n ";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			//checkNext(scanner, Kind.IDENTIFIER, 0, 6, 1, 1);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testBiswajitCase2() throws LexicalException {
		String input = " \r \n ";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			//checkNext(scanner, Kind.IDENTIFIER, 0, 6, 1, 1);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	

	@Test
	public void test() throws LexicalException {
		String input = "//abc12312345\r\n";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			//checkNext(scanner, Kind.IDENTIFIER, 0, 6, 1, 1);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	

	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "\" greetings  ";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
}
