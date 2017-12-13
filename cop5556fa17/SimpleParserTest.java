package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParserTest {

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
	 * Simple test case with an empty program.  This test 
	 * expects an SyntaxException because all legal programs must
	 * have at least an identifier
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	
	/** Another example.  This is a legal program and should pass when 
	 * your parser is implemented.
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */

	@Test
	public void testDecVariable() throws LexicalException, SyntaxException {
		String input = "prog int k; int xy; boolean bb;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDecVariable1() throws LexicalException, SyntaxException {
		String input = "prog int k=5; int xy=(+-xy[x,y]); boolean bb;";//expression->unary->unarynot+-->primary->identorpixel->selector
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDecImage() throws LexicalException, SyntaxException {
		String input = "prog image img <- \"this is sparta\"   ; image abc;\nimage img <- @!+x;";//source->expression->unary->unarynot+-->unary->unarynot+-
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDecImage1() throws LexicalException, SyntaxException {
		//image dec->expression->unary->unarynot+-->primary->functionapp->functionname__expression->unary->unarynot+- ->__expression->unary->unarynot+-
		String input = "prog image [sin(atan[!x,+-y]),123] xyz <- @sss;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDecImage2() throws LexicalException, SyntaxException {
	    String input = "suman image [abc, bcd] abcd <- \"suman\";";	//imagedec->expression__ ->source->string
	    show(input);
	        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	        show(scanner);   //Display the Scanner
	        Parser parser = new Parser(scanner);  //
	        parser.parse();
	    }


	@Test
	public void testImageDeclaration2() throws LexicalException, SyntaxException {
	    String input = "suman image [abc, bcd] abcd;";
	    show(input);
	        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	        show(scanner);   //Display the Scanner
	        Parser parser = new Parser(scanner);  //
	        parser.parse();
	    }


	@Test
	public void testImageDeclaration3() throws LexicalException, SyntaxException {
	    String input = "suman image abcd <- @ abcd;";//imagedec->source->expression
	    show(input);
	        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
	        show(scanner);   //Display the Scanner
	        Parser parser = new Parser(scanner);  //
	        parser.parse();
	    }
	
	
	@Test
	public void testDecImageInvalid() throws LexicalException, SyntaxException {
		try {
			String input = "prog image img <- \"this is sparta\"   ; image abc;\nimage img = @!+x;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		} catch (SyntaxException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			assertEquals(OP_ASSIGN,e.t.kind);
		}
	}
	
	@Test
	public void testDecSource() throws LexicalException, SyntaxException {
		//source->exprsn->orexprsn(a)->andexprsn->unary->unarynotplus->(a)andexprsn->eqexprsn(b)->unarynotplus->(b)eqexprsn->rel->primary->orexprsn
		String input = "prog url link=\"http://www.quotesstreet.com\"; file d=abc;url link=@+-x|+x&(y)|x;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
	public void testDecSourceInvalid() throws LexicalException, SyntaxException {
		
		String input = "prog url link=\"http://www.quotesstreet.com\"; file d=abc;url link=@+-x|+x&(y)|;";
		show(input);
		try{Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		catch(SyntaxException s){
			assertEquals(Kind.SEMI,s.t.kind);
		}
	}

	@Test
	public void testDecMix1() throws LexicalException, SyntaxException {
		String input = "prog int k=!s; int xy; boolean bb;url link=\"http://www.quotesstreet.com\"; image img <- \"this is sparta\"   ; image abc;";
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	
	@Test
	public void testStatement1() throws LexicalException, SyntaxException {
		String input = "prog abc -> SCREEN;";//imageoutstmnt->sink
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	
	@Test
    public void testStatementLhs() throws LexicalException, SyntaxException {
        String input = "prog abc[[x,y]]=+-1234*abc123;";
        show(input);
            Scanner scanner = new Scanner(input).scan();
            show(scanner);
            Parser parser = new Parser(scanner);  //
            parser.parse();
    }
	
	@Test
    public void testProgram() throws LexicalException, SyntaxException {
        String input[]={"prog int k=!s; int xy; boolean bb;url link=\"http://www.quotesstreet.com\"; image img <- \"this is sparta\"   ; image abc;",
				"prog int abc = +-abc; boolean abc; boolean bc = true; ",
				"prog",
				"abc suman = +-!+1234*+-!abc[12345,!+-(!sin[x,R])]/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) //this is sparta\n< +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) & +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y)  |  +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) & +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) ?\n 12345 : abc; boolean bb=true;",
				"prog int xc; boolean bb"
				};
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				show(scanner);
				Parser parser = new Parser(scanner);  
				parser.parse();
				System.out.println("Parsed input: "+s);
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(EOF, e.t.kind);
		}
    }
	
	@Test
    public void testDeclaration() throws LexicalException, SyntaxException {
		String input[]={"int k=!s",
				"boolean xy=true",
				"int abc = +-x",
				"image [abc, bcd] abcd",
				"file suman = @ +-(x)",
				//"int + = abc",
				"int abc = +"
				
				};
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				show(scanner);
				Parser parser = new Parser(scanner);  
				parser.declaration();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(EOF, e.t.kind);
		}
    }
	
	@Test
    public void testVariableDeclaration() throws LexicalException, SyntaxException {
		String input[]={"int k=!s",
				"boolean xy=true",
				"int abc = +-x",
				"int abc",
				"int x"
				};
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				show(scanner);
				Parser parser = new Parser(scanner);  
				parser.varDeclaration();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(KW_x, e.t.kind);
		}
    }
	
	@Test
    public void testSourceSinkDeclaration() throws LexicalException, SyntaxException {
		String input[]={"url link=\"http://www.quotesstreet.com\"",
				"file suman=abc12345",
				"file suman = @ +-(x)",
				"url link = @|x"
				};
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				Parser parser = new Parser(scanner);  
				parser.sourceSinkDeclaration();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(OP_OR, e.t.kind);
		}
    }
	
	
	@Test
    public void testStatement() throws LexicalException, SyntaxException {
		String input[]={"suman -> sourav",
				"suman <- \r\n \"Suman is Singh\"",
				"suman = +-!+1234*+-!abc[12345,!+-(!sin[x,R])]/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) //this is sparta\n< +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) & +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y)  |  +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) & +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) ?\n 12345 : abc",
				"suman -> iSCREEN"
				};
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				Parser parser = new Parser(scanner);  
				parser.statement();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(IDENTIFIER, e.t.kind);
		}
    }
	
	@Test
	public void testImageDeclaration() throws LexicalException, SyntaxException {
		String input[]={"image abcd <- @ abcd",
				"image [abc, bcd] abcd",
				"image img <- \"this is sparta\" ",
				"image img",
				"image [abc * abc] abc "
				};
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				Parser parser = new Parser(scanner);  
				parser.imageDeclaration();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(RSQUARE, e.t.kind);
		}
	}
	
	
	@Test
	public void testSink() throws LexicalException, SyntaxException {
		String input[]={"suman",
				"SCREEN",
				"!suman_12345",
				};
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				Parser parser = new Parser(scanner);  
				parser.sink();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(OP_EXCL, e.t.kind);
		}
	}
	
	
	
	//s!uman
	@Test
	public void testExpression() throws LexicalException, SyntaxException {
		String input[]={"+-!+1234*+-!abc[12345,!+-(!sin[x,R])]/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) //this is sparta\n< +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) & +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y)  |  +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) & +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) ?\n 12345 : abc",
				"+-!12345&abc/+x%-y+r-X+Y-Z==+A!=-R!+(x)&x+abc?+x;-y",
				"+-!+1234*+-!x/+-!+abc%+-!+(y)+!+-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) & +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y)  |  +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) & +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) == +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) != +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) < +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) >= +-!+1234*+-!x/+-!+abc%+-!+(y)++-!+1234*+-!x/+-!+abc%+-!+(y) ?? 12345 : abc"};
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				//show(scanner);
				Parser parser = new Parser(scanner);  
				parser.expression();
				System.out.println("Parsed input: "+s);
			}
		}
		catch(SyntaxException e){
			System.out.println(e+","+e.t.pos_in_line);
			assertEquals(OP_Q, e.t.kind);
		}
	}
	
	
	@Test
	public void testUnary() throws LexicalException, SyntaxException {
		String input[]={"+-x","-+y","--++!"};
		try{
			Scanner scanner;
			for(String s:input)
			{
				show(s);
				scanner = new Scanner(s).scan();  
				show(scanner);
				Parser parser = new Parser(scanner);  
				parser.unaryExpression();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(EOF, e.t.kind);
		}
	}
	@Test
	public void testUnaryNotPlusMinus() throws LexicalException, SyntaxException {
		String input[]={"sourav[abc,+-!12345]","!+-atan[suman,sin(sourav)]","x","y","r","a","X","Y","Z","A","R","DEF_X","DEF_Y","!!!!!!!+*a"};//suman[sin[sourav,singh],b]
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				Parser parser = new Parser(scanner);  
				parser.unaryExpressionNotPlusMinus();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(OP_TIMES, e.t.kind);
		}
	}
	
	@Test
	public void testPrimary() throws LexicalException, SyntaxException {
		String input[]={"12345","(abc)","atan[suman,sin(sourav)]","atan[suman,sin[sourav)]"};//suman[sin[sourav,singh],b]
		try{
			Scanner scanner;
			for(String s:input)
			{
				show(s);
				scanner = new Scanner(s).scan();  
				show(scanner);
				Parser parser = new Parser(scanner);  
				parser.primary();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(RPAREN, e.t.kind);
		}
	}
	
	@Test
	public void testIdentOrPixelSelector() throws LexicalException, SyntaxException {
		String input[]={"suman999","sourav[abc,+-!12345]","suman[sin,b]"};//suman[sin[sourav,singh],b]
		try{
			Scanner scanner;
			for(String s:input)
			{
				show(s);
				scanner = new Scanner(s).scan();  
				show(scanner);
				Parser parser = new Parser(scanner);  
				parser.identOrPixelSelectorExpression();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(COMMA, e.t.kind);
		}
	}
	
/*s*//*	@Test
	public void testLhsSelector() throws LexicalException, SyntaxException {
		String input="[r,A]";
		System.out.println(input.length());
		show(input);
		try{
			Scanner scanner = new Scanner(input).scan();  
			Parser parser = new Parser(scanner);  
			parser.lhsSelector();
		}
		catch(SyntaxException e){
			assertEquals(IDENTIFIER, e.t.kind);
		}
	}
*/	
	@Test
	public void testXySelector() throws LexicalException, SyntaxException {
		String input[]={"x,y","x,Y"};
		show(input);
		try{
			Scanner scanner;
			for(String s:input)
			{
				show(s);
				scanner = new Scanner(s).scan();  
				show(scanner);
				Parser parser = new Parser(scanner);  
				parser.xySelector();
			}
		}
		catch(SyntaxException e){
			assertEquals(KW_Y, e.t.kind);
		}
	}
	
	@Test
	public void testRaSelector() throws LexicalException, SyntaxException {
		String input[]={"r,A","r,a"};
		try{
			Scanner scanner;
			for(String s:input)
			{
				show(s);
				scanner = new Scanner(s).scan();  
				Parser parser = new Parser(scanner);  
				parser.raSelector();
			}
		}
		catch(SyntaxException e){
			assertEquals(KW_a, e.t.kind);
		}
	}
	
	@Test
	public void testSelector() throws LexicalException, SyntaxException {
		String input[]={"+-x,(!+suman[sin[x,y],atan(+-12345>46)])","+-xy!s,abc"};//
		try{
			Scanner scanner;
			for(String s:input)
			{
				scanner = new Scanner(s).scan();  
				Parser parser = new Parser(scanner);  
				parser.selector();
			}
		}
		catch(SyntaxException e){
			System.out.println(e);
			assertEquals(OP_EXCL, e.t.kind);
		}
	}
	
	/**
	 * This example invokes the method for expression directly. 
	 * Effectively, we are viewing Expression as the start
	 * symbol of a sub-language.
	 *  
	 * Although a compiler will always call the parse() method,
	 * invoking others is useful to support incremental development.  
	 * We will only invoke expression directly, but 
	 * following this example with others is recommended.  
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression1() throws SyntaxException, LexicalException {
		String input = "abc123+xy|z | 123 | 456 * suman * sourav / singh / sss % +-x % mno";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	@Test
	public void expression2() throws SyntaxException, LexicalException {
		String input = "x != y";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	@Test
	public void expressioninfra() throws SyntaxException, LexicalException {
		String input = "a:b:c";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();    
	}
	
	@Test
	public void andExpressionTest() throws SyntaxException, LexicalException {
		String input = "x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void orExpressionTest() throws SyntaxException, LexicalException {
		String input = "x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void expressionTest2() throws SyntaxException, LexicalException {
		String input = "a = (c > d) ? e : f ";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void imageDeclarationTest() throws SyntaxException, LexicalException {
		String input = "image [ 3 + 4, 5 + 6 ]flag <- \"nikita\"";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.imageDeclaration();
	}
	
	@Test
	public void expressionTest3() throws SyntaxException, LexicalException {
		String input = "flag1 + flag2";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void identOrPixelSelectorExpressionTest() throws SyntaxException, LexicalException {
		String input = "flag1";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.identOrPixelSelectorExpression();
	}
	
	@Test
	public void identOrPixelSelectorExpressionTes2() throws SyntaxException, LexicalException {
		String input = "flag1[1 + 2, 3 + 4]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.identOrPixelSelectorExpression();
	}
	
	@Test
	public void selectorTest() throws SyntaxException, LexicalException {
		String input = "1 + 2, 3 + 4";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.selector();
	}
	
	@Test
	public void unaryExpressionNotPlusMinusTest() throws SyntaxException, LexicalException {
		String input = "flag1[1 + 2, 3 + 4]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.unaryExpressionNotPlusMinus();
	}
	
	@Test
	public void unaryExpressionTest() throws SyntaxException, LexicalException {
		String input = "flag1[1 + 2, 3 + 4]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.unaryExpression();
	}
	
	@Test
	public void sourceSinkDeclarationTest() throws SyntaxException, LexicalException {
		String input = "url flag = \"nikita\"";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.sourceSinkDeclaration();
	}
	/*
	@Test
	public void functionApplicationTest1() throws SyntaxException, LexicalException {
		String input = "polar_r (num1 + num2)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.functionApplication();
	}
	
	@Test
	public void functionApplicationTest2() throws SyntaxException, LexicalException {
		String input = "cos [num1 + num2, num3 - num4]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.functionApplication();
	}
	
	@Test
	public void functionApplicationTest3() throws SyntaxException, LexicalException {
		String input = "sin [&, num3 - num4]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.functionApplication();// Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	*/
	@Test
	public void primaryTest1() throws SyntaxException, LexicalException {
		String input = "1234";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.primary();
	}
	
	@Test
	public void primaryTest2() throws SyntaxException, LexicalException {
		String input = "(num1 + num2)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.primary();
	}
	
	@Test
	public void primaryTest3() throws SyntaxException, LexicalException {
		String input = "(num1 + num2";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.primary();// Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void primaryTest4() throws SyntaxException, LexicalException {
		String input = "(&)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.primary();// Parse the program
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void primaryTest5() throws SyntaxException, LexicalException {
		String input = "sin (num1 + num2)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.primary();
	}
	
	@Test
	public void primaryTest6() throws SyntaxException, LexicalException {
		String input = "nikita (num1 + num2)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.primary();
			} 
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void unaryExpressionNotPlusMinusTest1() throws SyntaxException, LexicalException {
		String input = "DEF_Y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.unaryExpressionNotPlusMinus();
	}
	
	@Test
	public void unaryExpressionNotPlusMinusTest2() throws SyntaxException, LexicalException {
		String input = "sin [";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.unaryExpressionNotPlusMinus();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void identOrPixelSelectorExpressionTest1() throws SyntaxException, LexicalException {
		String input = "nikita";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.identOrPixelSelectorExpression();
	}
	
	@Test
	public void identOrPixelSelectorExpressionTest2() throws SyntaxException, LexicalException {
		String input = "nikita[num1 + num2, num3 - num4]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.identOrPixelSelectorExpression();
	}
	
	@Test
	public void identOrPixelSelectorExpressionTest3() throws SyntaxException, LexicalException {
		String input = "nikita[num1 + num2 num3 - num4]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.identOrPixelSelectorExpression();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void unaryExpressionNotPlusMinusTest3() throws SyntaxException, LexicalException {
		String input = "!-x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.unaryExpressionNotPlusMinus();
	}
	
	@Test
	public void unaryExpressionTest1() throws SyntaxException, LexicalException {
		String input = "+!x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.unaryExpression();
	}
	
	@Test
	public void multExpressionTest1() throws SyntaxException, LexicalException {
		String input = "+!x%-3";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.multExpression();
	}
	
	@Test
	public void wholeTest1() throws SyntaxException, LexicalException {
		String input = "nikitaIdentifier";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void wholeTest2() throws SyntaxException, LexicalException {
		String input = "nikita int flag = num1 + num2;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void wholeTest3() throws SyntaxException, LexicalException {
		String input = "nikita boolean flag = num1 + num2;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void wholeTest4() throws SyntaxException, LexicalException {
		String input = "nikita boolean flag;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void wholeTest5() throws SyntaxException, LexicalException {
		String input = "nikita boolean flag";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void wholeTest6() throws SyntaxException, LexicalException {
		String input = "nikita float flag";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void selectorTestDashboard1() throws SyntaxException, LexicalException {
		String input = "a*b,c*d";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.selector();
	}
	
	@Test
	public void selectorTestDashboard2() throws SyntaxException, LexicalException {
		String input = "(a*b,c*d";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void selectorTestDashboard3() throws SyntaxException, LexicalException {
		String input = "(a*b),c*d";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.selector();
	}
	
	@Test
	public void expressionDashboardRandom() throws SyntaxException, LexicalException {
		String input = "++++x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void wholeWhatsappRandom() throws SyntaxException, LexicalException {
		String input = "xyz=dmg;";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void expressionWhatsappRandom() throws SyntaxException, LexicalException {
		String input = "( (tyri==dead)? false: what_the_hell?)";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testcaseEOF() throws SyntaxException, LexicalException {
		String input = "prog @expr k=12;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	  @Test
	    public void grammCheck() throws SyntaxException, LexicalException
	    {
	   	 String input = "nikita nikita -> saxena;";
	   	 show(input);
	   	 Scanner scanner = new Scanner(input).scan();  
	   	 show(scanner);   
	   	 Parser parser = new Parser(scanner);  
	   	 parser.program();    
	    }

	// testing boolean, int and boolean expressions together.
		@Test//(expected=SyntaxException.class)
		public void testDec4() throws LexicalException, SyntaxException {
			String input = "prog int abc; boolean bcd = 3; int xyz = 3+4; boolean abc = x & y;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing image.
		@Test//(expected=SyntaxException.class)
		public void testDec5() throws LexicalException, SyntaxException {
			String input = "prog image xyz;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing keyword in place of identifier.
		@Test(expected=SyntaxException.class)
		public void testDec6() throws LexicalException, SyntaxException {
			String input = "prog image x;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing image keyword with only expression.
		@Test//(expected=SyntaxException.class)
		public void testDec7() throws LexicalException, SyntaxException {
			String input = "prog image [a+b,b+c] xyz;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing image keyword with only source and identifier.
		@Test//(expected=SyntaxException.class)
		public void testDec8() throws LexicalException, SyntaxException {
			String input = "prog image xyz <- abc;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing image keyword with only source and string literal.
		@Test//(expected=SyntaxException.class)
		public void testDec9() throws LexicalException, SyntaxException {
			String input = "prog image xyz <- \"abc\";";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing image keyword with only source and @Expression.
		@Test//(expected=SyntaxException.class)
		public void testDec10() throws LexicalException, SyntaxException {
			String input = "prog image xyz <- @ a==b+c;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing image keyword with source and expressions.
		@Test//(expected=SyntaxException.class)
		public void testDec11() throws LexicalException, SyntaxException {
			String input = "prog image [abc==xyx, a==b+c/e] xyz <- @a==b+c;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing boolean, int and boolean expressions together.
		@Test(expected=SyntaxException.class)
		public void testDec12() throws LexicalException, SyntaxException {
			String input = "prog int abc; boolean bcd = ; int xyz = 3+4; boolean abc = x & y;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing sourceSinkDeclaration.
		@Test//(expected=SyntaxException.class)
		public void testDec13() throws LexicalException, SyntaxException {
			String input = "abc url xyz = \"Hello\";";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing sourceSinkDeclaration.
		@Test//(expected=SyntaxException.class)
		public void testDec14() throws LexicalException, SyntaxException {
			String input = "abc url xyz = @x==z+e;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing sourceSinkDeclaration.
		@Test//(expected=SyntaxException.class)
		public void testDec15() throws LexicalException, SyntaxException {
			String input = "abc url xyz = test;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing sourceSinkDeclaration.
		@Test//(expected=SyntaxException.class)
		public void testDec16() throws LexicalException, SyntaxException {
			String input = "abc file xyz = \"test\";";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing sourceSinkDeclaration.
		@Test//(expected=SyntaxException.class)
		public void testDec17() throws LexicalException, SyntaxException {
			String input = "abc file xyz = @x&y&z;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing sourceSinkDeclaration.
		@Test//(expected=SyntaxException.class)
		public void testDec18() throws LexicalException, SyntaxException {
			String input = "abc file xyz = test1;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing sourceSinkDeclaration.
		@Test//(expected=SyntaxException.class)
		public void testDec19() throws LexicalException, SyntaxException {
			String input = "abc ab = a-+b;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testStat1() throws LexicalException, SyntaxException {
			String input = "abc abc = a|b&c;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testStat2() throws LexicalException, SyntaxException {
			String input = "abc xyz [[x,y]] =  f==z;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test(expected=SyntaxException.class)
		public void testStat3() throws LexicalException, SyntaxException {
			String input = "abc xyz [[r,a]] =  f==z;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testStat4() throws LexicalException, SyntaxException {
			String input = "abc xyz [[r,A]] =  f==z;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testStat5() throws LexicalException, SyntaxException {
			String input = "abc xyz -> two; x1z -> screen;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testStat6() throws LexicalException, SyntaxException {
			String input = "abc xyz <- two; x1z <- screen;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testExpr1() throws LexicalException, SyntaxException {
			String input = "abc int xy = sin(3==4);";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testExpr2() throws LexicalException, SyntaxException {
			String input = "abc int xy = sin[sin(z==x+y/s), atan(cos(z==x))];";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testExpr3() throws LexicalException, SyntaxException {
			String input = "abc int ac = cos[sin(a + b / c), cos(a + atan(x))];";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		// testing statement.
		@Test//(expected=SyntaxException.class)
		public void testExpr4() throws LexicalException, SyntaxException {
			String input = "abc int ac = a*b+c/g-+h/g+rfg%6+8;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		/**
		 * This example invokes the method for expression directly. 
		 * Effectively, we are viewing Expression as the start
		 * symbol of a sub-language.
		 *  
		 * Although a compiler will always call the parse() method,
		 * invoking others is useful to support incremental development.  
		 * We will only invoke expression directly, but 
		 * following this example with others is recommended.  
		 * 
		 * @throws SyntaxException
		 * @throws LexicalException
		 */
		@Test
		public void expressionas1() throws SyntaxException, LexicalException {
			String input = "2";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		// Testing expressions.
		@Test
		public void expressionas2() throws SyntaxException, LexicalException {
			String input = "f + b / c";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}

		@Test
		public void expression3() throws SyntaxException, LexicalException {
			String input = "a + b / c";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression4() throws SyntaxException, LexicalException {
			String input = "x?x:y";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression5() throws SyntaxException, LexicalException {
			String input = "d == x | y";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression6() throws SyntaxException, LexicalException {
			String input = "d == x & y";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression7() throws SyntaxException, LexicalException {
			String input = "a == b + c";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression8() throws SyntaxException, LexicalException {
			String input = "a==b";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression9() throws SyntaxException, LexicalException {
			String input = "a:b:c";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression10() throws SyntaxException, LexicalException {
			String input = "a-+b";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
	}

