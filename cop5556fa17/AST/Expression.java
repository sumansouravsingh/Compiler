package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Expression extends ASTNode {
	private Type Type;

	public Type getType() {
		return Type;
	}

	public void setType(Type type) {
		this.Type = type;
	}

	public Expression(Token firstToken) {
		super(firstToken);
	}

}
