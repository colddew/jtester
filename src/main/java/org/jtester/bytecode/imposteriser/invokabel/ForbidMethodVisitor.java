package org.jtester.bytecode.imposteriser.invokabel;

import mockit.external.asm.MethodAdapter;
import mockit.external.asm.MethodVisitor;
import mockit.external.asm.Opcodes;

import org.jtester.exception.ForbidCallException;

public class ForbidMethodVisitor extends MethodAdapter {

	public ForbidMethodVisitor(MethodVisitor mv) {
		super(mv);
	}

	@Override
	public void visitCode() {
		visitMethodInsn(Opcodes.INVOKESTATIC, "SecurityChecker", "checkSecurity", "()V");
	}

	public void forbidMethod() {
		throw new ForbidCallException("this api is forbid called.");
	}
}
