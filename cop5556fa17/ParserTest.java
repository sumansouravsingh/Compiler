package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.AST.*;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Simple test case with an empty program. This test expects an exception
	 * because all legal programs must have at least an identifier
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string. Parsing should fail
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the tokens
		Parser parser = new Parser(scanner); //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse();; //Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e);  //catch the exception and show it
			throw e;  //rethrow for Junit
		}
	}


	@Test
	public void testNameOnly() throws LexicalException, SyntaxException {
		String input = "prog";  //Legal program with only a name
		show(input);            //display input
		Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
		show(scanner);    //display the tokens
		Parser parser = new Parser(scanner);   //create parser
		Program ast = parser.parse();          //parse program and get AST
		show(ast);                             //Display the AST
		assertEquals(ast.name, "prog");        //Check the name field in the Program object
		assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
	}

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);
	}
	
	@Test
	public void testVariableDeclaration() throws LexicalException, SyntaxException {
		String input = "suman int abc; boolean val=true;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "suman"); 
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("abc", dec.name);
		assertNull(dec.e);
		dec = (Declaration_Variable) ast.decsAndStatements.get(1);  
		assertEquals(KW_boolean, dec.type.kind);
		assertEquals("val", dec.name);
		assertEquals("true",dec.e.firstToken.getText());		
	}
	
	@Test
	public void testSourceSinkDeclaration() throws LexicalException, SyntaxException {
		String input = "suman url urlLink = \"suman.jpg\"; file fileName= @123;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "suman"); 
		Declaration_SourceSink dec = (Declaration_SourceSink) ast.decsAndStatements.get(0);  
		assertEquals(KW_url, dec.type);
		assertEquals("urlLink", dec.name);
		Source_StringLiteral sc = (Source_StringLiteral)dec.source;
		assertEquals("suman.jpg",sc.firstToken.getText());
		dec = (Declaration_SourceSink) ast.decsAndStatements.get(1);  
		assertEquals(KW_file, dec.type);
		Source_CommandLineParam scs = (Source_CommandLineParam)dec.source;
		assertEquals("fileName", dec.name);
		assertEquals("123",scs.paramNum.firstToken.getText());		
	}
	
	
	@Test
	public void testImageDeclaration() throws LexicalException, SyntaxException {
		String input = "prog image [abc,bcd] k; image [123,b] suman <- \"suman.png\";";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);  
		assertEquals(KW_image, dec.firstToken.kind);
		assertEquals("abc", dec.xSize.firstToken.getText());
		assertEquals("bcd", dec.ySize.firstToken.getText());
		assertEquals("k", dec.name);
		
		dec = (Declaration_Image) ast.decsAndStatements.get(1);  
		assertEquals(KW_image, dec.firstToken.kind);
		assertEquals("123", dec.xSize.firstToken.getText());
		assertEquals("b",dec.ySize.firstToken.getText());
		assertEquals("suman", dec.name);
		Source_StringLiteral s = (Source_StringLiteral) dec.source;
		assertEquals(STRING_LITERAL, s.firstToken.kind);
		assertEquals("suman.png", s.firstToken.getText());
	}
	
	@Test 
	public void testAssignmentStatement() throws SyntaxException, LexicalException {
		//String input = "ident1 url ident2 = ;";
		String input = "prog assignment [[x,y]] = +-!x;";
		show(input);
		
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		Statement_Assign dec = (Statement_Assign) ast.decsAndStatements.get(0);  
		assertEquals("assignment", dec.firstToken.getText());
		LHS l = dec.lhs;
		assertEquals("assignment", l.firstToken.getText());
		assertEquals(KW_x,l.index.e0.firstToken.kind);
		assertEquals(KW_y,l.index.e1.firstToken.kind);
		Expression e = dec.e;
		assertEquals("+", e.firstToken.getText());
			
	}
	
	
	
}