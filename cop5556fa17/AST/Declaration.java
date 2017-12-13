package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Declaration extends ASTNode {
	
	private Type Type;

	public Type getType() {
		return Type;
	}

	public void setType(Type type) {
		this.Type = type;
	}


	public Declaration(Token firstToken) {
		super(firstToken);
	}



}
