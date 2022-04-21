import java.util.*;
import ast.*;

class CgenEmitDispatchTables extends CgenVisitor {

    // Emit the dispatch table of a class
    @Override
    Void visit(CgenNode v) {
      /* TODO */
      Cgen.emitter.emitDispTableRef(v.getClassNode().getName());
      Cgen.emitter.emitLabel();
      
      // CgenNode _node = v;
      // List<Symbol> unique = new ArrayList<>();
      // List<Symbol> classNodes = new ArrayList<>();
      // while (_node.getClassNode().getName() != TreeConstants.No_class) {
      //   for (FeatureNode featureNode : _node.getClassNode().getFeatures()) {
      //     if (featureNode instanceof MethodNode) {
      //       if (!unique.contains(((MethodNode) featureNode).getName())) {
      //         unique.add(((MethodNode) featureNode).getName());
      //         classNodes.add(_node.getClassNode().getName());
      //       }
      //     }
      //   }
      //   _node = _node.getParentNd();
      // }
      
      // for (int index = unique.size()-1; index > -1; index--) {
      //   Cgen.emitter.emitWordforDispatchNode(classNodes.get(index), unique.get(index));
      // }

      // List<List<Symbol>> unique = new ArrayList<>();
      // System.out.println(unique);

      LinkedHashMap<Symbol, Symbol> unique = getParentAttrAndMethods(v.getParentNd());
      for (FeatureNode featureNode : v.getClassNode().getFeatures()) {
        if (featureNode instanceof MethodNode) {
          unique.put(((MethodNode) featureNode).getName(), v.getClassNode().getName());
          // Cgen.emitter.emitWordforDispatchNode(v.getClassNode().getName(), ((MethodNode) featureNode).getName());
        }
      };

      for (Map.Entry<Symbol, Symbol> features : unique.entrySet()) {
        Cgen.emitter.emitWordforDispatchNode(features.getValue(), features.getKey());
      }
      super.visit(v);
      return null;
    }
 
    LinkedHashMap<Symbol, Symbol> getParentAttrAndMethods(CgenNode parent) {
      if (parent.getClassNode().getName() == TreeConstants.No_class) {
        return new LinkedHashMap<Symbol, Symbol>();
      }
      LinkedHashMap<Symbol, Symbol> first = getParentAttrAndMethods(parent.getParentNd());
      LinkedHashMap<Symbol, Symbol> second = emitWordDispatchNode(parent);
      // HashMap<Symbol, Symbol> _final = new HashMap<Symbol, Symbol>();
      // _final.putAll(second);
      // _final.putAll(first);
      return combine(first, second);
    }
//new HashMap<Symbol, ClassNode>() {{put(_mnode.getReturn_type(), _node);}});
    LinkedHashMap<Symbol, Symbol> emitWordDispatchNode(CgenNode parent) {
      LinkedHashMap<Symbol, Symbol> features = new LinkedHashMap<Symbol, Symbol>();
      for (FeatureNode featureNode : parent.getClassNode().getFeatures()) {
        if (featureNode instanceof MethodNode) {
          features.put(((MethodNode) featureNode).getName(), parent.getClassNode().getName());
          // Cgen.emitter.emitWordforDispatchNode(parent.getClassNode().getName(), ((MethodNode) featureNode).getName());
        }
      }
      return features;
    }

    LinkedHashMap<Symbol, Symbol> combine(LinkedHashMap<Symbol, Symbol> h1, LinkedHashMap<Symbol, Symbol> h2) {
      LinkedHashMap<Symbol, Symbol> _final = new LinkedHashMap<Symbol, Symbol>();
      for (Map.Entry<Symbol, Symbol> features : h1.entrySet()) {
        _final.put(features.getKey(), features.getValue());
      }
      for (Map.Entry<Symbol, Symbol> features : h2.entrySet()) {
        _final.put(features.getKey(), features.getValue());
      }
      return _final;
    }
}