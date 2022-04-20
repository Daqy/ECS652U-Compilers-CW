import ast.*;

class CgenEmitDispatchTables extends CgenVisitor {

    // Emit the dispatch table of a class
    @Override
    Void visit(CgenNode v) {
      /* TODO */
      Cgen.emitter.emitDispTableRef(v.getClassNode().getName());
      Cgen.emitter.emitLabel();
      getParentAttrAndMethods(v.getParentNd());
      for (FeatureNode featureNode : v.getClassNode().getFeatures()) {
        if (featureNode instanceof MethodNode) {
          Cgen.emitter.emitWordforDispatchNode(v.getClassNode().getName(), ((MethodNode) featureNode).getName());
        }
      };
      super.visit(v);
      // for(CgenNode e: v.getChildren()) {
      //   Cgen.emitter.emitDispTableRef(e.getClassNode().getName());
      //   Cgen.emitter.emitLabel();
      //   getParentAttrAndMethods(e.getParentNd());
      //   for (FeatureNode featureNode : e.getClassNode().getFeatures()) {
      //     if (featureNode instanceof MethodNode) {
      //       Cgen.emitter.emitWordforDispatchNode(e.getClassNode().getName(), ((MethodNode) featureNode).getName());
      //     }
      //   };
      // }
      // for (CgenNode e: v.getChildren()) {
      //   super.visit(e);
      // }
      return null;
    }

    String getParentAttrAndMethods(CgenNode parent) {
      if (parent.getClassNode().getName() == TreeConstants.No_class) {
        return "";
      }
      return (getParentAttrAndMethods(parent.getParentNd()) + emitWordDispatchNode(parent));
    }

    String emitWordDispatchNode(CgenNode parent) {
      for (FeatureNode featureNode : parent.getClassNode().getFeatures()) {
        if (featureNode instanceof MethodNode) {
          Cgen.emitter.emitWordforDispatchNode(parent.getClassNode().getName(), ((MethodNode) featureNode).getName());
        }
      }
      return "";
    }
}