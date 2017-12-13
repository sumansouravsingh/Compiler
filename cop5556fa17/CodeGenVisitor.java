package cop5556fa17;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
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
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.ImageFrame;
import cop5556fa17.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	final static int DEF_X=256,DEF_Y=256,Z=16777215;
	MethodVisitor mv; // visitor of method currently under construction
	FieldVisitor fv;
	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	BufferedImage imgBuffer;
	

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;
		System.out.println("Class name:"+className);
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		fv = cw.visitField(ACC_STATIC, "x", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "y", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "X", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "Y", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "r", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "a", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "R", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "A", "I", null, null);
		fv.visitEnd();
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
			
		}

		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction of main method
		mv.visitEnd();
		//fv.visitEnd();
		//terminate class construction
		cw.visitEnd();
		
		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		// TODO
			
		fv = cw.visitField(ACC_STATIC, declaration_Variable.name, CodeGenUtils.getJVMType(declaration_Variable.getType()), null, null);
		if(declaration_Variable.e!=null) {
			 declaration_Variable.e.visit(this, arg);
			 mv.visitFieldInsn(PUTSTATIC,className,declaration_Variable.name, CodeGenUtils.getJVMType(declaration_Variable.getType()));
		}
		fv.visitEnd();
		return declaration_Variable;
	}
	
	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		// TODO 
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		if(expression_Binary.getType() == Type.INTEGER) {
			if(expression_Binary.op==Kind.OP_PLUS) {
				mv.visitInsn(IADD);
			}
			else if(expression_Binary.op==Kind.OP_MINUS) {
				mv.visitInsn(ISUB);
			}
			else if(expression_Binary.op==Kind.OP_DIV) {
				mv.visitInsn(IDIV);
			}
			else if(expression_Binary.op==Kind.OP_MOD) {
				mv.visitInsn(IREM);
			}
			else if(expression_Binary.op==Kind.OP_TIMES) {
				mv.visitInsn(IMUL);
			}
			else if(expression_Binary.op==Kind.OP_AND) {
				mv.visitInsn(IAND);
			}
			else if(expression_Binary.op==Kind.OP_OR) {
				mv.visitInsn(IOR);
			}
		}
		else if(expression_Binary.getType() == Type.BOOLEAN) {
			Label trueCondition = new Label();
			Label falseCondition = new Label();
			if(expression_Binary.op==Kind.OP_AND) {
				mv.visitInsn(IAND);
			}
			else if(expression_Binary.op==Kind.OP_OR) {
				mv.visitInsn(IOR);
			}
			else {
				if(expression_Binary.op==Kind.OP_GT) {
					mv.visitJumpInsn(IF_ICMPGT, trueCondition);
	            }
				else if(expression_Binary.op==Kind.OP_LE) {
					mv.visitJumpInsn(IF_ICMPLE, trueCondition);
	            }
				else if(expression_Binary.op==Kind.OP_GE) {
					mv.visitJumpInsn(IF_ICMPGE, trueCondition);
	            }
				else if(expression_Binary.op==Kind.OP_LT) {
					mv.visitJumpInsn(IF_ICMPLT, trueCondition);
	            }
				else if(expression_Binary.op==Kind.OP_EQ) {
					mv.visitJumpInsn(IF_ICMPEQ, trueCondition);
	            }
				else if(expression_Binary.op==Kind.OP_NEQ) {
					mv.visitJumpInsn(IF_ICMPNE, trueCondition);
	            }
				else
					throw new UnsupportedOperationException("Error! Not Supported");
				
				
				mv.visitJumpInsn(IF_ICMPGT, trueCondition);
				mv.visitInsn(ICONST_0);
	            mv.visitJumpInsn(GOTO, falseCondition);
	            mv.visitLabel(trueCondition);
	            mv.visitInsn(ICONST_1);
	            mv.visitLabel(falseCondition);
			}
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.getType());
		return null;
	}
	
	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		// TODO 
		expression_Unary.e.visit(this, arg);
		if(expression_Unary.op == Kind.OP_PLUS) {
			//mv.visitInsn(arg0);
		}
		else if(expression_Unary.op == Kind.OP_MINUS) {
			mv.visitInsn(INEG);
		}
		else if(expression_Unary.op==Kind.OP_EXCL) {
			if(expression_Unary.getType()==Type.BOOLEAN) {
				Label trueCondition = new Label();
				Label falseCondition = new Label();
				mv.visitJumpInsn(IFEQ,trueCondition);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, falseCondition);
				mv.visitLabel(trueCondition);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(falseCondition);
			}
			else if(expression_Unary.getType()==Type.INTEGER) {
				mv.visitLdcInsn(Integer.MAX_VALUE);
				mv.visitInsn(IXOR);
			}
			
		}
		else throw new UnsupportedOperationException("Error in Expression Unary");
		
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.getType());
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if(!index.isCartesian()) {
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.name,ImageSupport.ImageDesc);
		expression_PixelSelector.index.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig, false);
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO 
		expression_Conditional.condition.visit(this, arg);
		Label trueCondition = new Label();
		Label falseCondition = new Label();
		mv.visitJumpInsn(IFNE, trueCondition);
		expression_Conditional.falseExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO,falseCondition);
		mv.visitLabel(trueCondition);
		expression_Conditional.trueExpression.visit(this, arg);
		mv.visitLabel(falseCondition);
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.trueExpression.getType());
		return null;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		fv=cw.visitField(ACC_STATIC, declaration_Image.name,
				ImageSupport.ImageDesc, null,null);
		if(declaration_Image.source!=null) {
			declaration_Image.source.visit(this, arg);
		}	
		if(declaration_Image.source!=null&&declaration_Image.xSize!=null) {
			declaration_Image.xSize.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, "X","I");
			mv.visitFieldInsn(GETSTATIC, className, "X","I");
			declaration_Image.ySize.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, "Y","I");
			mv.visitFieldInsn(GETSTATIC, className, "Y","I");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
		}
		else if(declaration_Image.source!=null&&declaration_Image.xSize==null){
			mv.visitLdcInsn(ACONST_NULL);
			mv.visitLdcInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
		}
		else if(declaration_Image.source==null&&declaration_Image.xSize!=null){
			declaration_Image.xSize.visit(this, arg);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(PUTSTATIC, className, "X","I");
			declaration_Image.ySize.visit(this, arg);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(PUTSTATIC, className, "Y","I");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", 
			"(II)Ljava/awt/image/BufferedImage;",false);
		}
		else {
			mv.visitLdcInsn(DEF_X);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(PUTSTATIC, className, "X","I");
			mv.visitLdcInsn(DEF_X);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(PUTSTATIC, className, "Y","I");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", 
					ImageSupport.makeImageSig,false);
		}
		mv.visitFieldInsn(PUTSTATIC, className,declaration_Image.name,ImageSupport.ImageDesc);
		return null;
	}
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		// TODO (see comment )
		statement_In.source.visit(this, arg);
		if(statement_In.getDec().getType()==Type.INTEGER) {
			mv.visitMethodInsn(INVOKESTATIC,"java/lang/Integer","parseInt","(Ljava/lang/String;)I",false);	
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "I");
		}
		else if(statement_In.getDec().getType()==Type.BOOLEAN) {
			mv.visitMethodInsn(INVOKESTATIC,"java/lang/Boolean","parseBoolean","(Ljava/lang/String;)Z",false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "Z");
		}
		else if(statement_In.getDec().getType()==Type.IMAGE) {
			Declaration_Image img = (Declaration_Image) statement_In.getDec();
			if(img!=null&&img.xSize!=null) {
				mv.visitFieldInsn(GETSTATIC, className, "X","I");
				mv.visitMethodInsn(INVOKESTATIC,"java/lang/Integer","valueOf","(I)Ljava/lang/Integer;",false);
				mv.visitFieldInsn(GETSTATIC, className, "Y","I");
				mv.visitMethodInsn(INVOKESTATIC,"java/lang/Integer","valueOf","(I)Ljava/lang/Integer;",false);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, ImageSupport.ImageDesc);
			}
			else
			{
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name,ImageSupport.ImageDesc);
				mv.visitFieldInsn(GETSTATIC, className, statement_In.name,ImageSupport.ImageDesc);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, "X","I");
				mv.visitFieldInsn(GETSTATIC, className, statement_In.name,ImageSupport.ImageDesc);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getXSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, "Y","I");
			}
		}
			
		//throw new UnsupportedOperationException();
		return null;
	}
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		//if(source_StringLiteral.getType()==Type.FILE)
			mv.visitLdcInsn(source_StringLiteral.fileOrUrl);
		/*else if(source_StringLiteral.getType()==Type.URL) {
			mv.visitTypeInsn(NEW, "java/net/URL");
			mv.visitInsn(DUP);
			mv.visitLdcInsn(new String(source_StringLiteral.fileOrUrl));
			mv.visitMethodInsn(INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false);
			//mv.visitFieldInsn(PUTSTATIC, className, "url", "Ljava/net/URL;");
		}
		*/return null;
	}

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, "Ljava/lang/String;");
		return null;
	}

	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		fv=cw.visitField(ACC_STATIC, declaration_SourceSink.name,
				CodeGenUtils.getJVMType(declaration_SourceSink.getType()), null,null);	
		if(declaration_SourceSink.source!=null) {
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.name,
					CodeGenUtils.getJVMType(declaration_SourceSink.getType()));
		}
		return null;
	}
	
	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_IntLit.value);
		// CodeGenUtils.genLogTOS(GRADE, mv, TypeUtils.Type.INTEGER);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.function==Kind.KW_abs)
		{
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "abs", "(I)I", false);
		}
		else if(expression_FunctionAppWithExprArg.function==Kind.KW_log)
		{
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "log", "(I)I", false);
		}
		
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		// TODO HW6
		expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		if(expression_FunctionAppWithIndexArg.function==Kind.KW_cart_x)
		{
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", "(II)I", false);
		}
		else if(expression_FunctionAppWithIndexArg.function==Kind.KW_cart_y)
		{
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		else if(expression_FunctionAppWithIndexArg.function==Kind.KW_polar_a)
		{
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
		}
		else if(expression_FunctionAppWithIndexArg.function==Kind.KW_polar_r)
		{
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
		}
		
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		if(expression_PredefinedName.kind==Kind.KW_x)
		{
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
		}
		else if(expression_PredefinedName.kind==Kind.KW_y)
		{
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
		}
		else if(expression_PredefinedName.kind==Kind.KW_r)
		{
			mv.visitFieldInsn(GETSTATIC, className, "r", "I");
		}
		else if(expression_PredefinedName.kind==Kind.KW_a)
		{
			mv.visitFieldInsn(GETSTATIC, className, "a", "I");
		}
		else if(expression_PredefinedName.kind==Kind.KW_X)
		{
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
		}
		else if(expression_PredefinedName.kind==Kind.KW_Y)
		{
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
		}
		else if(expression_PredefinedName.kind==Kind.KW_Z)
		{
			mv.visitLdcInsn(Z);
		}
		else if(expression_PredefinedName.kind==Kind.KW_R)
		{
			mv.visitFieldInsn(GETSTATIC, className, "R", "I");
		}
		else if(expression_PredefinedName.kind==Kind.KW_A)
		{
			mv.visitFieldInsn(GETSTATIC, className, "A", "I");
		}
		else if(expression_PredefinedName.kind==Kind.KW_DEF_X)
		{
			mv.visitLdcInsn(DEF_X);
		}
		else if(expression_PredefinedName.kind==Kind.KW_DEF_Y)
		{
			mv.visitLdcInsn(DEF_Y);
		}
		return null;
	}
	
	/**
	 * For Integers and booleans, the only "sink"is the screen, so generate code
	 * to print to console. For Images, load the Image onto the stack and visit
	 * the Sink which will generate the code to handle the image.
	 */

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		// TODO in HW5:  only INTEGER and BOOLEAN
		// TODO HW6 remaining cases	
		if(statement_Out.getDec().getType()==Type.IMAGE) {
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name,ImageSupport.ImageDesc);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().getType());
			statement_Out.sink.visit(this, arg);
		}else {
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, 
						CodeGenUtils.getJVMType(statement_Out.getDec().getType()));
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().getType());
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", 
						"("+CodeGenUtils.getJVMType(statement_Out.getDec().getType())+")V",false);
		}
		return null;
	}




	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		//TODO  (see comment)
		if(statement_Assign.lhs.getType()==Type.IMAGE) {
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name,ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "X", "I");
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name,ImageSupport.ImageDesc);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "Y", "I");
			
			if(statement_Assign.isCartesian())
			{}
			else {
				mv.visitFieldInsn(GETSTATIC, className, "X", "I");
				mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, "R", "I");
				mv.visitInsn(ICONST_0);
				mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, "A", "I");
			}
			Label l0 = new Label();
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			mv.visitJumpInsn(GOTO,l0);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			Label l2 = new Label();
			mv.visitJumpInsn(GOTO, l2);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			if(statement_Assign.isCartesian()) {
				statement_Assign.e.visit(this, arg);
				statement_Assign.lhs.visit(this, arg);
			}
			else {
				
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, "r", "I");
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, "a", "I");
				
				statement_Assign.e.visit(this, arg);
				mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false);
			}
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitJumpInsn(IF_ICMPLT, l3);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
			mv.visitJumpInsn(IF_ICMPLT, l1);
		}
		else {
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
		}
		return null;	
	}
	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		//TODO  (see comment)
		if(lhs.getType()==Type.IMAGE) {
			mv.visitFieldInsn(GETSTATIC, className,lhs.name,ImageSupport.ImageDesc);
			if(lhs.index!=null)
			{	
				lhs.index.visit(this, arg);
			}
			else{
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false);
		}
		else {
			if(lhs.index!=null)
			{	
				lhs.index.visit(this, arg);
			}
			mv.visitFieldInsn(PUTSTATIC,className,lhs.name, CodeGenUtils.getJVMType(lhs.getType()));
		}
		return null;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		//TODO HW6
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeFrame", ImageSupport.makeFrameSig, false);
		mv.visitInsn(POP);
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//TODO HW6
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, "Ljava/lang/String;");
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig, false);
		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		//TODO
		mv.visitLdcInsn(expression_BooleanLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		//TODO
		mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, 
				CodeGenUtils.getJVMType(expression_Ident.getType()));
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.getType());
		return null;
	}

}