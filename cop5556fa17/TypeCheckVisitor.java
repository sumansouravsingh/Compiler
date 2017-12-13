package cop5556fa17;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

import java.net.URL;
import java.util.HashMap;
public class TypeCheckVisitor implements ASTVisitor {
	HashMap<String,Declaration> symbolTable;
	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;
		public SemanticException(Token t, String message) {
			super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
			this.t = t;
		}
	}		
		
	public TypeCheckVisitor() {
		// TODO Auto-generated constructor stub
		symbolTable = new HashMap<String,Declaration>();
	}
		
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		if(lookupType(declaration_Variable.name)!=null)
			throw new SemanticException(declaration_Variable.firstToken,"Error in Declaration Variable. Duplicate variable: "+declaration_Variable.name);
		declaration_Variable.setType(TypeUtils.getType(declaration_Variable.type));
		if(declaration_Variable.e!=null)
		{	
			declaration_Variable.e.visit(this, arg);
		}
		insert(declaration_Variable.name, declaration_Variable);
		if(declaration_Variable.e!=null && declaration_Variable.getType() != declaration_Variable.e.getType())
			throw new SemanticException(declaration_Variable.firstToken,"Error in Declaration Variable. Expression Type: "
					+declaration_Variable.e.getType()+" and Variable Type: "+ declaration_Variable.getType()+" should be same: ");
		return declaration_Variable;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		if((expression_Binary.op==Kind.OP_EQ || expression_Binary.op==Kind.OP_NEQ) 
			|| ((expression_Binary.op==Kind.OP_GE || expression_Binary.op==Kind.OP_GT || 
			expression_Binary.op==Kind.OP_LT || expression_Binary.op==Kind.OP_LE)
				&& expression_Binary.e0.getType()==Type.INTEGER))
			expression_Binary.setType(Type.BOOLEAN);
		else if((expression_Binary.op==Kind.OP_AND || expression_Binary.op==Kind.OP_OR)
				&& (expression_Binary.e0.getType()==Type.INTEGER|| expression_Binary.e0.getType() == Type.BOOLEAN))
			expression_Binary.setType(expression_Binary.e0.getType());
		else if((expression_Binary.op==Kind.OP_DIV || expression_Binary.op==Kind.OP_MINUS 
				|| expression_Binary.op==Kind.OP_MOD || expression_Binary.op==Kind.OP_PLUS
				|| expression_Binary.op==Kind.OP_POWER || expression_Binary.op==Kind.OP_TIMES)
				&& expression_Binary.e0.getType()==Type.INTEGER)
			expression_Binary.setType(Type.INTEGER);
		else expression_Binary.setType(Type.NONE);
		if(expression_Binary.e0.getType()!=expression_Binary.e1.getType())
			throw new SemanticException(expression_Binary.firstToken,"Error in Expression_Binary! Type of e0: "+expression_Binary.e0.getType()+" and e1:"+expression_Binary.e1.getType()+" should be same");
		if(expression_Binary.getType()==Type.NONE)
			throw new SemanticException(expression_Binary.firstToken,"Error in Expression_Binary! Type should not be none");
		return expression_Binary;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_Unary.e.visit(this, arg);
		if(expression_Unary.op == Kind.OP_EXCL &&(expression_Unary.e.getType()==Type.BOOLEAN||expression_Unary.e.getType()==Type.INTEGER))
			expression_Unary.setType(expression_Unary.e.getType());
		else if(expression_Unary.e.getType() == Type.INTEGER &&(expression_Unary.op==Kind.OP_PLUS||expression_Unary.op==Kind.OP_MINUS))
			expression_Unary.setType(Type.INTEGER);
		else
			throw new SemanticException(expression_Unary.firstToken,"Error in Expression_Unary! Type should not be none");
		return expression_Unary;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		boolean a=false,b=false;
		Expression_PredefinedName p =null;
		try{
			p = (Expression_PredefinedName)index.e0;
			a=index.e0.firstToken.getText().equals("r");
		}
		catch(Exception e){
			a=false;
		}
		try{
			p = (Expression_PredefinedName)index.e0;
			b=index.e1.firstToken.getText().equals("a");
		}
		catch(Exception e){
			b=false;
		}
		if(index.e0.getType()!=Type.INTEGER || index.e1.getType()!=Type.INTEGER)
			throw new SemanticException(index.firstToken,"Error in Index! Type of e0: "+index.e0.getType()+" and e1: "+index.e1.getType()+" should be INTEGER");
		index.setCartesian(!(a&&b));
		return index;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		if(expression_PixelSelector.index!=null)
			expression_PixelSelector.index.visit(this, arg);
		Type typeOfExp=Type.NONE;
		if(lookupType(expression_PixelSelector.name)!=null)
			typeOfExp= lookupType(expression_PixelSelector.name).getType();
		if(typeOfExp==Type.IMAGE){
			expression_PixelSelector.setType(Type.INTEGER);
		}
		else if(expression_PixelSelector.index==null){
			expression_PixelSelector.setType(typeOfExp);
		}
		else
		{	
			throw new SemanticException(expression_PixelSelector.firstToken,"Error in Expression_PixelSelector! Requirements of expression_Conditional failed");
		}
		return expression_PixelSelector;
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_Conditional.condition.visit(this, arg);
		expression_Conditional.trueExpression.visit(this, arg);
		expression_Conditional.falseExpression.visit(this, arg);
		if(expression_Conditional.condition.getType() != Type.BOOLEAN || 
				(expression_Conditional.trueExpression.getType() != expression_Conditional.falseExpression.getType()))
			throw new SemanticException(expression_Conditional.firstToken,"Error in Expression_Conditional! Requirements of expression_Conditional failed");
		Type typeOfExp = expression_Conditional.trueExpression.getType();
		expression_Conditional.setType(typeOfExp);
		return expression_Conditional;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(lookupType(declaration_Image.name)!=null)
			throw new SemanticException(declaration_Image.firstToken, "Error in Declaration_Image. Duplicate variable: "+declaration_Image.name);
		if(declaration_Image.xSize!=null)
		{	declaration_Image.xSize.visit(this, arg);
			if(declaration_Image.ySize!=null)
				declaration_Image.ySize.visit(this, arg);
			else
				throw new SemanticException(declaration_Image.firstToken, "Error in Declaration_Image! ySize cannot be null if xSize is not null");	
			if(declaration_Image.xSize.getType()!=Type.INTEGER || declaration_Image.ySize.getType()!=Type.INTEGER){
				throw new SemanticException(declaration_Image.firstToken, "Error in Declaration_Image! "
						+ "Type of xSize: "+declaration_Image.xSize.getType()+", and ySize :"+declaration_Image.ySize.getType()+", should be INTEGER");	
			}
		}
		
		if(declaration_Image.source!=null)
			declaration_Image.source.visit(this, arg);
		declaration_Image.setType(Type.IMAGE);
		insert(declaration_Image.name, declaration_Image);
		return declaration_Image;
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		try{
			URL url=new URL(source_StringLiteral.fileOrUrl);
			source_StringLiteral.setType(Type.URL);
		}
		catch(Exception e){
			source_StringLiteral.setType(Type.FILE);
		}
		return source_StringLiteral;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		source_CommandLineParam.paramNum.visit(this, arg);
		//source_CommandLineParam.setType(source_CommandLineParam.paramNum.getType());
		source_CommandLineParam.setType(Type.NONE);
		if(source_CommandLineParam.paramNum.getType()!=Type.INTEGER)
			throw new SemanticException(source_CommandLineParam.firstToken,"Error in Source_CommandLineParam! Type: "+source_CommandLineParam.getType()+", should be INTEGER");	
		return source_CommandLineParam;
	}

	//CHECK HERE
	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		if(lookupType(source_Ident.name)!=null)
			source_Ident.setType(lookupType(source_Ident.name).getType());
		else
			source_Ident.setType(Type.NONE);
		if(source_Ident.getType() != Type.FILE && source_Ident.getType()!=Type.URL)
			throw new SemanticException(source_Ident.firstToken,"Error in Source_Ident! Type: "+source_Ident.getType()+", should be FILE or URL");
		return source_Ident;
	}

	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		if(lookupType(declaration_SourceSink.name)!=null)
			throw new SemanticException(declaration_SourceSink.firstToken,"Error in Declaration_SourceSink! Duplicate variable: "+declaration_SourceSink.name);
		declaration_SourceSink.setType(TypeUtils.getType(declaration_SourceSink.firstToken));
		declaration_SourceSink.source.visit(this, arg);
		if(declaration_SourceSink.source.getType()!=declaration_SourceSink.getType()
				&& declaration_SourceSink.source.getType()!=Type.NONE)
			throw new SemanticException(declaration_SourceSink.firstToken,"Error in Declaration_SourceSink! Type of Source:"+
		declaration_SourceSink.source.getType()+", and SourceSink:"+declaration_SourceSink.getType()+", should be equal");
		insert(declaration_SourceSink.name, declaration_SourceSink);
		return declaration_SourceSink;
		// TODO Auto-generated method stub
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_IntLit.setType(Type.INTEGER);
		return expression_IntLit;
	}
	
	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.arg.getType() != Type.INTEGER)
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken,
			"Error in Expression_FunctionAppWithExprArg! Type: "+expression_FunctionAppWithExprArg.arg.getType()+", should be INTEGER");
		expression_FunctionAppWithExprArg.setType(Type.INTEGER);
		return expression_FunctionAppWithExprArg;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_FunctionAppWithIndexArg.arg.visit(this, arg);
		expression_FunctionAppWithIndexArg.setType(Type.INTEGER);
		return expression_FunctionAppWithIndexArg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_PredefinedName.setType(Type.INTEGER);
		return expression_PredefinedName;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Type type=Type.NONE;
		if(lookupType(statement_Out.name)!=null)
			type=lookupType(statement_Out.name).getType();
		else
			throw new SemanticException(statement_Out.firstToken, "Error in Statement_Out! Declaration cannot be null.");
		statement_Out.sink.visit(this, arg);
		if(!(((type == Type.INTEGER || type == Type.BOOLEAN)&&statement_Out.sink.getType()==Type.SCREEN)||
				(type == Type.IMAGE && (statement_Out.sink.getType()==Type.SCREEN || statement_Out.sink.getType()==Type.FILE))))
			throw new SemanticException(statement_Out.firstToken,"Error in Statement_Out! Requirements for Statement Out failed");
		statement_Out.setDec(lookupType(statement_Out.name));
		return statement_Out;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		statement_In.source.visit(this, arg);
		statement_In.setDec(lookupType(statement_In.name));
		return statement_In;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		statement_Assign.lhs.visit(this, arg);
		statement_Assign.e.visit(this, arg);
		if(statement_Assign.lhs.getType() != statement_Assign.e.getType())
		{
			if(statement_Assign.lhs.getType() == Type.IMAGE && statement_Assign.e.getType() == Type.INTEGER)
			{}
			else
				throw new SemanticException(statement_Assign.firstToken,"Error in Statement_Assign! Type of LHS: "+statement_Assign.lhs.getType()+" and Expression: "+statement_Assign.e.getType()+" is not same");
			}statement_Assign.setCartesian(statement_Assign.lhs.isCartesian());
		return statement_Assign;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(lhs.index!=null)
			lhs.index.visit(this, arg);
		lhs.setDec(lookupType(lhs.name));
		Type type = Type.NONE;
		if(lhs.getDec()!=null)
			type = lhs.getDec().getType();
		lhs.setType(type);
		boolean res=false;
		if(lhs.index!=null)
		{
			res = lhs.index.isCartesian();
		}
		lhs.setCartesian(res);
		return lhs;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		sink_SCREEN.setType(Type.SCREEN);
		return sink_SCREEN;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		if(lookupType(sink_Ident.name)!=null){
			sink_Ident.setType(lookupType(sink_Ident.name).getType());
		}
		else
			sink_Ident.setType(Type.NONE);
		if(sink_Ident.getType()!=Type.FILE)
			throw new SemanticException(sink_Ident.firstToken,"Error in Sink_Ident! Type: "+ sink_Ident.getType()+" should of sink ident should be FILE");
		return sink_Ident;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_BooleanLit.setType(Type.BOOLEAN);
		return expression_BooleanLit;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(lookupType(expression_Ident.name)!=null)
			expression_Ident.setType(lookupType(expression_Ident.name).getType());
		else expression_Ident.setType(Type.NONE);
		return expression_Ident;
	}
	
	public Declaration lookupType(String name){
		if(symbolTable.containsKey(name))
			return symbolTable.get(name);
		else
			return null;
	}
	
	public boolean insert(String name, Declaration dec){
		if(symbolTable.containsKey(name)){
			return false;
		}
		else{
			symbolTable.put(name, dec);
		}
		return true;
	}
	
	public void printTable(){
		System.out.println(symbolTable);
	}
}
