class CgenEmitPrototypeObjects extends CgenVisitor {
    @Override
    Void visit(CgenNode v) {
        Cgen.emitter.codePrototypeObject(v.env);
        super.visit(v);
        return null;
    }
}
