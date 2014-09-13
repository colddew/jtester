package org.jtester.bytecode.imposteriser.invokabel;


//import org.objectweb.asm.ClassAdapter;
//import org.objectweb.asm.ClassVisitor;
//import org.objectweb.asm.MethodVisitor;

public class ForbidClassReader{// extends ClassAdapter {

//	public ForbidClassReader(ClassVisitor cv) {
//		super(cv);
//	}
//
//	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
//			final String[] exceptions) {
//		if (filter.contains(name)) {
//			return null;
//		} else {
//			MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
//			return new ForbidMethodVisitor(mv);
//		}
//	}
//
//	private static Set<String> filter = new HashSet<String>() {
//		private static final long serialVersionUID = 5922564786978233189L;
//
//		{
//			this.add("getClass");
//			this.add("hashCode");
//			this.add("finalize");
//			this.add("equals");
//			this.add("toString");
//		}
//	};
}
