package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Sink extends ASTNode {
	
	private Type Type;

	public Type getType() {
		return Type;
	}

	public void setType(Type typeName) {
		this.Type = typeName;
	}

	public Sink(Token firstToken) {
		super(firstToken);
	}
	

}
