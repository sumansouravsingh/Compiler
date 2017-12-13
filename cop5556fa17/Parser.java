package cop5556fa17;

import java.util.*;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.AST.*;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			System.out.println("\\**************************************************************\\ \n \t Tokens Parsed: \n");
			for(Token tk:list)
				System.out.println(tk.kind);
			System.out.println("\\**************************************************************\\");
			
			System.out.println("\n\n\\**************************************************************\\ \n \t Tokens left to be Parsed: \n");
			for(Token ts:allTokens)
				System.out.println(ts.kind);
			System.out.println("\\**************************************************************\\");
			
			
			this.t = t;
		}

	}

	Scanner scanner;
	Token t;
	ArrayList<Token> list;
	ArrayList<Token> allTokens;
	
	Parser(Scanner scanner) {
		this.scanner = scanner;
		int i=0;
		this.allTokens = new ArrayList<>();
		while(i<scanner.tokens.size()-1){
			allTokens.add(scanner.tokens.get(i++));
		}
		t = scanner.nextToken();
		this.list = new ArrayList<>();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program prog = program();
		matchEOF();
		return prog;
	}
	
	
	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	public Program program() throws SyntaxException {
		//TODO  implement this
		if(isTokenListEmpty())
			throw new SyntaxException(t,"Token List is Empty");
		ArrayList<ASTNode> list = new ArrayList<ASTNode>();
		Token firstToken=t;
		ASTNode node = null;
		match(IDENTIFIER,"program");
		while(!t.isKind(Kind.EOF))
		{
			if(t.isKind(KW_int)||t.isKind(Kind.KW_boolean)||t.isKind(KW_url)||t.isKind(KW_file)||t.isKind(KW_image))
			{
				node = declaration();
			}
			else if(t.isKind(IDENTIFIER))
			{
				node = statement();
			}
			else throw new SyntaxException(t,"Wrong token! Expected Identifier, boolean, int, url, file, image. Got: "+t.kind);
			match(SEMI,"program");
			list.add(node);
		}
		Program p = new Program(firstToken,firstToken, list);
		return p;
	}

	/**
	 * Declaration :: = VariableDeclaration | ImageDeclaration | SourceSinkDeclaration
	 * @throws SyntaxException
	 */
	public Declaration declaration() throws SyntaxException{
		// TODO Auto-generated method stub
		Declaration dec = null;
		if(t.isKind(KW_int)||t.isKind(KW_boolean)){
			dec = varDeclaration();
		}
		else if(t.isKind(KW_url)||t.isKind(KW_file)){
			dec=sourceSinkDeclaration();
		}
		else if(t.isKind(KW_image)){
			dec=imageDeclaration();
		}
		else throw new SyntaxException(t, "Error in Declaration. Expected int, boolean,url,file or image. Got: "+t.kind);
		return dec;
	}
	
	/**
	 * VariableDeclaration ::= VarType IDENTIFIER ( = Expression | Empty )
	 * @throws SyntaxException
	 */
	protected Declaration_Variable varDeclaration() throws SyntaxException {
		Token firstToken = t;
		consume();
		Token name = t;
		match(IDENTIFIER,"varDeclaration");
		if(t.isKind(OP_ASSIGN)){
			consume();
			return new Declaration_Variable(firstToken, firstToken, name, expression());
		}
		else return new Declaration_Variable(firstToken, firstToken, name, null);
	}

	/**
	 * SourceSinkDeclaration ::= SourceSinkType IDENTIFIER OP_ASSIGN Source
	 * @throws SyntaxException
	 */
	protected Declaration_SourceSink sourceSinkDeclaration() throws SyntaxException {
		//TODO implement this.
		Token firstToken = t;
		if(t.isKind(KW_url)||t.isKind(KW_file)){
			consume();
		}
		else
		{
			throw new SyntaxException(t, "Error in variable declaration. Expected url or file. Got: "+t.kind);
		}
		Token name = t;
		match(IDENTIFIER,"sourceSinkDeclaration");
		match(OP_ASSIGN,"sourceSinkDeclaration");
		Declaration_SourceSink decSourceSink = new Declaration_SourceSink(firstToken,firstToken,name,source());
		return decSourceSink;
	}
	
	/**
	 * source ::= STRING_LITERAL | OP_AT Expression | Source ::= IDENTIFIER
	 * @throws SyntaxException
	 */
	Source source() throws SyntaxException {
		//TODO implement this.
		Token firstToken = t;
		if(t.isKind(STRING_LITERAL))
		{
			consume();
			return new Source_StringLiteral(firstToken, firstToken.getText());
		}
		else if(t.isKind(Kind.IDENTIFIER)){
			consume();
			return new Source_Ident(firstToken, firstToken);
		}
		else if(t.isKind(OP_AT)){
			consume();
			Expression e=expression();
			return new Source_CommandLineParam(firstToken, e);
		}
		else
			 throw new SyntaxException(t,"Wrong token in source! Expected: STRING_LITERAL, IDENTIFIER, OP_AT. Found:"+t.kind);
	}
	/**
	 * ImageDeclaration ::= KW_image (LSQUARE Expression COMMA Expression RSQUARE | null) IDENTIFIER ( OP_LARROW Source | null )
	 * @throws SyntaxException
	 */
	protected Declaration_Image imageDeclaration() throws SyntaxException {
		//TODO implement this.
		Token firstToken=t;
		Token name = null;
		consume();
		Expression xSize=null, ySize=null;
		Source source = null;
		if(t.isKind(LSQUARE)){
			consume();
			xSize=expression();
			match(COMMA,"imageDeclaration");
			ySize=expression();
			match(Kind.RSQUARE,"imageDeclaration");
		}
		if(t.isKind(IDENTIFIER)){
			name=t;
			consume();
			if(t.isKind(Kind.OP_LARROW)){
				consume();
				source = source();
			}
		}
		else
			throw new SyntaxException(t, "Error in Image Declaration. Expected Identifier. Got:"+t.kind);
		return new Declaration_Image(firstToken, xSize, ySize, name, source);
	}
	
	/**
	 * Statement ::= AssignmentStatement | ImageOutStatement | ImageInStatement
	 * @throws SyntaxException
	 */
	protected Statement statement() throws SyntaxException{
		// TODO Auto-generated method stub
		Token firstToken = t;
		match(IDENTIFIER,"statement");
		Expression e=null;
		LHS lhs = null;
		if(t.isKind(LSQUARE)){
			lhs = lhs(firstToken);
			match(OP_ASSIGN,"statement");
			e = expression();
		}
		else if(t.isKind(OP_ASSIGN)){
			lhs = new LHS(firstToken, firstToken, null);
			consume();
			e=expression();
		}
		else if(t.isKind(OP_RARROW)){
			consume();
			Sink sink = sink();
			return new Statement_Out(firstToken, firstToken, sink);
		}
		else if(t.isKind(OP_LARROW)){
			consume();
			Source source = source();
			return  new Statement_In(firstToken, firstToken, source);
		}
		else throw new SyntaxException(t,"Exception in statement. Expected LSQUARE, OP_ASSIGN, OP_RARROW, OP_LARROW. Got: "+t.kind);
		return new Statement_Assign(firstToken, lhs, e);
		
	}

	
	/**
	 * Sink ::= IDENTIFIER | KW_SCREEN
	 * @throws SyntaxException
	 */
	protected Sink sink() throws SyntaxException{
		// TODO Auto-generated method stub
		Token firstToken = t;
		Token name = t;
			if(t.isKind(KW_SCREEN)) {
				consume();
				return new Sink_SCREEN(firstToken);
			}
			else if(t.isKind(IDENTIFIER)){
				consume();
				return new Sink_Ident(firstToken,name);
			}
			
			throw new SyntaxException(t,"Error in sink. Expected: IDENTIFIER or KW_SCREEN. GOT: "+t.kind); 
		
	}

	public LHS lhs(Token firstToken) throws SyntaxException{
		consume();
		Index index=lhsSelector();
		match(RSQUARE,"lhs");
		return new LHS(firstToken, firstToken, index);
	}
	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * Our test cases may invoke this routine directly to support incremental development.
	 * @return 
	 * @throws SyntaxException
	 */
	public Expression expression() throws SyntaxException {
		//TODO implement this.
		Token firstToken = t;
		Expression condition = null;
		condition=orExpression();
		if(t.isKind(OP_Q))
		{
			consume();
			Expression trueExpression = expression();
			match(OP_COLON,"expression");
			Expression falseExpression = expression();
			return new Expression_Conditional(firstToken, condition, trueExpression, falseExpression);
		}
		return condition;
	}
	
	/**
	 * OrExpression :: = AndExpression (OP_OR AndExpression)*
	 * @throws SyntaxException
	 */
	Expression orExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Token op = null;
		Expression e1=null;
		Expression e0 = andExpression();
		while(t.isKind(OP_OR)){
			op=t;
			consume();
			e1=andExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1); 
		}
		return e0;
	}
		
	/**
	 * AndExpression :: = EqExpression ( OP_AND EqExpression )*
	 * @throws SyntaxException
	 */
	Expression andExpression() throws SyntaxException{
		// TODO Auto-generated method stub
		Token firstToken = t;
		Token op = null;
		Expression e1=null;
		Expression e0=eqExpression();
		while(t.isKind(OP_AND)){
			op=t;
			consume();
			e1=eqExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1); 
		}
		return e0;
		
	}
	
	/**
	 * EqExpression ::= RelExpression ( (OP_EQ | OP_NEQ ) RelExpression )
	 * @return *
	 * @throws SyntaxException
	 */
	Expression eqExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token firstToken = t;
		Token op = null;
		Expression e1=null;
		Expression e0=relExpression();
		while(t.isKind(OP_EQ)||t.isKind(OP_NEQ)){
			op=t;
			consume();
			e1=relExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1); 
		}
		return e0;
	}
	
	/**
	 * RelExpression ::= AddExpression ( ( OP_LT | OP_GT | OP_LE | OP_GE ) AddExpression
	 * @return 
	 * @throws SyntaxException
	 */
	Expression relExpression() throws SyntaxException {
		Token firstToken = t;
		Token op = null;
		Expression e1=null;
		Expression e0=addExpression();
		while(t.isKind(OP_LT)||t.isKind(OP_GT)||t.isKind(OP_LE)||t.isKind(OP_GE)){
			op=t;
			consume();
			e1=addExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1); 
		}
		return e0;
	}
	
	/**
	 *AddExpression ::= MultExpression ( (OP_PLUS | OP_MINUS ) MultExpression )
	 * @return *
	 * 
	 * @throws SyntaxException
	 */
	Expression addExpression() throws SyntaxException {
		Token firstToken = t;
		Token op = null;
		Expression e1=null;
		Expression e0=multExpression();
		while(t.isKind(OP_PLUS)||t.isKind(OP_MINUS)){
			op=t;
			consume();
			e1=multExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1); 
		}
		return e0;
	}
		
	/**
	 * MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV | OP_MOD ) UnaryExpression )
	 * @return *
	 * @throws SyntaxException
	 */
	Expression multExpression() throws SyntaxException{
		Token firstToken = t;
		Token op = null;
		Expression e1=null;
		Expression e0=unaryExpression();
		while(t.isKind(OP_TIMES)||t.isKind(OP_DIV)||t.isKind(OP_MOD)){
			op=t;
			consume();
			e1=unaryExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1); 
		}
		return e0;
	}
	
	/**
	 * UnaryExpression ::= OP_PLUS UnaryExpression | OP_MINUS UnaryExpression | UnaryExpressionNotPlusMinus
	 * @return 
	 * @throws SyntaxException
	 */
	Expression unaryExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e = null;
		if(t.isKind(OP_PLUS) || t.isKind(OP_MINUS)){
			Token op=t;
			consume();
			e = unaryExpression();
			return new Expression_Unary(firstToken,op,e);
		}
		else{
			return unaryExpressionNotPlusMinus();
		}
	}
	
	/**
	 * UnaryExpressionNotPlusMinus ::= OP_EXCL UnaryExpression | Primary | IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X | KW_Y | KW_Z | KW_A | KW_R | KW_DEF_X | KW_DEF_Y
	 * @throws SyntaxException
	 */
	Expression unaryExpressionNotPlusMinus() throws SyntaxException{
		Token firstToken = t;
		Token op = null;
		Expression e = null;
		if(t.isKind(IDENTIFIER)){
			e	= identOrPixelSelectorExpression();
		}
		else if(t.isKind(INTEGER_LITERAL)||t.isKind(LPAREN)||t.isKind(BOOLEAN_LITERAL)||t.isKind(KW_sin)||t.isKind(KW_cos)||t.isKind(KW_atan)||t.isKind(KW_abs)||t.isKind(KW_cart_x)||t.isKind(KW_cart_y)||t.isKind(KW_polar_a)||t.isKind(KW_polar_r))
		{
			e= 	primary();
		}
		else if(t.isKind(KW_x)||t.isKind(KW_y)||t.isKind(KW_r)||t.isKind(KW_a)||t.isKind(KW_X)||t.isKind(KW_Y)||t.isKind(KW_Z)||t.isKind(KW_A)||t.isKind(KW_R)||t.isKind(KW_DEF_X)||t.isKind(KW_DEF_Y)){
			op=t;
			consume();
			e = new Expression_PredefinedName(firstToken, op.kind); 
		}
		else if(t.isKind(OP_EXCL)){
			op=t;
			consume();
			e=unaryExpression();
			return new Expression_Unary(firstToken,op,e);
		}
		else
			 throw new SyntaxException(t,"Error in unaryExpressionNotPlusMinus. Expected unarExpressionNotPlusMinus tokens. Got: "+t.kind);
		return e;
	}
	
	/**
	 * Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication
	 * @throws SyntaxException
	 */
	public Expression primary() throws SyntaxException{
		Token firstToken = t;
		Expression e0=null;
		if(t.isKind(INTEGER_LITERAL)){
			int val=t.intVal();
			consume();
			return new Expression_IntLit(firstToken, val);
		}
		else if(t.isKind(LPAREN)){
			consume();
			Expression e=expression();
			match(RPAREN,"primary");
			return e;
		}
		else if(t.isKind(BOOLEAN_LITERAL)){
			boolean val = false;
			if(t.getText().equals("true"))
				val=true;
			consume();
			return new Expression_BooleanLit(firstToken, val);
		}
		else e0 = functionApplication();
		return e0;
	}
	
	/**
	 * IdentOrPixelSelectorExpression::= IDENTIFIER LSQUARE Selector RSQUARE | IDENTIFIER
	 * @throws SyntaxException
	 */
	public Expression identOrPixelSelectorExpression() throws SyntaxException{
		Token firstToken = t;
		Index index = null;
		Token name = t;
		match(IDENTIFIER,"identOrPixelSelectorExpression");	
		if(t.isKind(LSQUARE)){
			consume();
			index=selector();
			match(RSQUARE,"identOrPixelSelectorExpression");
			return new Expression_PixelSelector(firstToken, name, index);
		}
		return new Expression_Ident(firstToken, name);
	}
	/**
	 * FunctionApplication ::= FunctionName LPAREN Expression RPAREN | FunctionName LSQUARE Selector RSQUARE
	 * @throws SyntaxException
	 */
	public Expression functionApplication() throws SyntaxException{
		Token firstToken = t;
		functionName();
		if(t.isKind(LPAREN)){
			consume();
			Expression e=expression();
			match(RPAREN,"functionApplication");
			return new Expression_FunctionAppWithExprArg(firstToken, firstToken.kind, e);
		}
		else if(t.isKind(LSQUARE)){
			consume();
			Index ind = selector();
			match(RSQUARE,"functionApplication");
			return new Expression_FunctionAppWithIndexArg(firstToken, firstToken.kind, ind);
		}
		else throw new SyntaxException(t, "Exception in functionApplication. Expected LPAREN or LSQUARE. Got: "+t.kind);
	}
		
	/**
	 * FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | LW_polar_r
	 * @return 
	 * @throws SyntaxException
	 */
	protected void functionName() throws SyntaxException{
		if(t.isKind(KW_sin)||t.isKind(KW_cos)||t.isKind(KW_atan)||t.isKind(KW_abs)||t.isKind(KW_cart_x)||t.isKind(KW_cart_y)||t.isKind(KW_polar_a)||t.isKind(KW_polar_r))
		{
			consume();
		}
		else throw new SyntaxException(t, "Exception in functionName. Expected functionName token. Got: "+t.kind);
	}
	/** LhsSelector ::= LSQUARE ( XySelector | RaSelector ) RSQUARE
	 * @return 
	 * @throws SyntaxException
	*/
	protected Index lhsSelector() throws SyntaxException{
		match(LSQUARE,"lhsSelector");
		Index index=null;
		if(t.isKind(KW_x)){
			index=xySelector();
		}
		else if(t.isKind(KW_r)){
			index=raSelector();
		}
		else throw new SyntaxException(t, "Execption in lhsSelector. Expected x or r. Got: "+t.kind);
		match(RSQUARE,"lhsSelector");
		return index;
		
		
	}
	/** XySelector ::= KW_x COMMA KW_y
	 * @throws SyntaxException
	*/
	protected Index xySelector() throws SyntaxException{
		Token firstToken = t;
		Expression_PredefinedName e0=null, e1 = null;
		if(t.isKind(KW_x)){
			e0=new Expression_PredefinedName(firstToken, t.kind);
		}
		match(KW_x,"xySelector");
		match(COMMA,"xySelector");
		if(t.isKind(KW_y)){
			e1=new Expression_PredefinedName(t, t.kind);
		}
		match(KW_y,"xySelector");
		return new Index(firstToken, e0, e1);
	
	}
	
	/** RaSelector ::= KW_r , KW_A
	 * @throws SyntaxException
	*/
	protected Index raSelector() throws SyntaxException{
		Token firstToken = t;
		Expression_PredefinedName e0=null, e1 = null;
		if(t.isKind(KW_r)){
			e0=new Expression_PredefinedName(firstToken, t.kind);
		}
		match(KW_r,"raSelector");
		match(COMMA,"raSelector");
		if(t.isKind(KW_a)){
			e1=new Expression_PredefinedName(t, t.kind);
		}
		match(KW_a,"raSelector");
		return new Index(firstToken, e0, e1);
	}
	
	/** Selector ::= Expression COMMA Expression
	 * @return 
	 * @throws SyntaxException
	*/
	protected Index selector() throws SyntaxException{
		Token firstToken = t;
		Expression e0=expression();
		match(COMMA,"selector");
		Expression e1=expression();
		return new Index(firstToken, e0, e1);
	}		
			
	
	protected void match(Kind k, String name) throws SyntaxException{
		
		if(t.isKind(k))
		{
			consume();
		}
		else
			throw new SyntaxException(t, "Token Error! In method: "+name+".\nFound: "+t.kind+", Expected token: "+k+", pos in line:"+t.pos_in_line);
	}
			
	private void consume() {
		// TODO Auto-generated method stub
		this.list.add(t);
		this.allTokens.remove(t);
		t=scanner.nextToken();
	}
	
	private boolean isTokenListEmpty(){
		return this.allTokens.isEmpty();
	}
	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
}
