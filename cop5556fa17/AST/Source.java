package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Source extends ASTNode{

	private Type Type;

	public Type getType() {
		return Type;
	}

	public void setType(Type Type) {
		this.Type = Type;
	}

	public Source(Token firstToken) {
		super(firstToken);
	}
	
	
}
