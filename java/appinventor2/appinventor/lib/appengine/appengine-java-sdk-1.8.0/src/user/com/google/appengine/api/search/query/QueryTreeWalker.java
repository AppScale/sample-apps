// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.appengine.api.search.query;

import org.antlr.runtime.tree.Tree;

/**
 * The walking of the query tree. This class takes care of visiting
 * a tree resulting from parsing a query. As it traverses the tree
 * it calls appropriate methods of the visitor, set at the construction
 * time. The class uses a depth-first search, visiting all children
 * of a node, before visiting the node. The visit is done by calling
 * an appropriate method of the visitor. Typical code should match
 * the following pattern:
 * <pre>
 * class MyVisitor implements QueryTreeVisitor {
 *   ...
 * }
 * class MyContext extends QueryTreeContext&lt;MyContext&gt; {
 *   ...
 *   &#64;Override
 *   protected MyContext newChildContext() {
 *     return new MyContext();
 *   }
 * }
 *
 * MyContext context = new MyContext();
 * QueryTreeWalker&lt;MyContext&gt; walker = new QueryTreeWalker&lt;MyContext&gt;(new MyVisitor());
 * Tree root = parser.query(queryStr);
 * walker.walk(root, context);
 * // retrieve whatever information you need from context
 * </pre>
 *
 * @param <T> the context used by the visitor
 */
public class QueryTreeWalker<T extends QueryTreeContext<T>> {

  private final QueryTreeVisitor<T> visitor;

  /**
   * Creates a new query walker that calls the given {@code visitor}.
   *
   * @param visitor the visitor to be called by this walker
   */
  public QueryTreeWalker(QueryTreeVisitor<T> visitor) {
    this.visitor = visitor;
  }

  /**
   * @param tree the tree to be walked
   * @param context the context in which the tree is walked
   */
  public void walk(Tree tree, T context) throws QueryTreeException {
    walkInternal(flatten(tree, null), context);
  }

  /**
   * Walks the tree and updates the context. This is a depth-first search,
   * always exploring children of the {@code tree} in the order of their
   * index 0 ... n - 1, where n is the number of children. Once all children
   * of the {@code node} are visited, the appropriate visitor's method
   * is called
   *
   * @param tree the tree to be walked
   * @param context the context of the visit
   */
  private void walkInternal(Tree tree, T context) {
    switch (tree.getType()) {
      case QueryLexer.CONJUNCTION:
        walkChildren(tree, context);
        visitor.visitConjunction(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.DISJUNCTION:
        walkChildren(tree, context);
        visitor.visitDisjunction(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.SEQUENCE:
        walkChildren(tree, context);
        visitor.visitSequence(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.NEGATION:
        walkChildren(tree, context);
        visitor.visitNegation(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.HAS:
        walkChildren(tree, context);
        visitor.visitContains(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.EQ:
        walkChildren(tree, context);
        visitor.visitEqual(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.LT:
        walkChildren(tree, context);
        visitor.visitLessThan(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.LE:
        walkChildren(tree, context);
        visitor.visitLessOrEqual(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.GT:
        walkChildren(tree, context);
        visitor.visitGreaterThan(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.GE:
        walkChildren(tree, context);
        visitor.visitGreaterOrEqual(tree, context);
        postBooleanExpression(context);
        break;
      case QueryLexer.FUZZY:
        walkInternal(tree.getChild(0), context);
        visitor.visitFuzzy(tree, context);
        break;
      case QueryLexer.LITERAL:
        walkInternal(tree.getChild(0), context);
        visitor.visitLiteral(tree, context);
        break;
      case QueryLexer.VALUE:
        visitor.visitValue(tree, context);
        break;
      case QueryLexer.FUNCTION:
        walkChildren(tree.getChild(1), context);
        visitor.visitFunction(tree, context);
        break;
      case QueryLexer.GLOBAL:
        visitor.visitGlobal(tree, context);
        break;
      default:
        visitor.visitOther(tree, context);
    }
  }

  protected void postBooleanExpression(T context) {
    context.setReturnType(QueryTreeContext.Type.BOOL);
    context.setKind(QueryTreeContext.Kind.EXPRESSION);
  }

  private void walkChildren(Tree parent, T context) {
    for (int i = 0; i < parent.getChildCount(); ++i) {
      walkInternal(parent.getChild(i), context.addChild());
    }
  }

  public static Tree simplify(Tree tree) {
    for (int i = 0; i < tree.getChildCount(); ++i) {
      Tree child = tree.getChild(i);
      Tree optimized = simplify(child);
      if (child != optimized) {
        tree.setChild(i, optimized);
      }
    }
    switch (tree.getType()) {
      case QueryLexer.CONJUNCTION:
      case QueryLexer.DISJUNCTION:
      case QueryLexer.SEQUENCE:
        if (tree.getChildCount() == 1) {
          return tree.getChild(0);
        }
        break;
    }
    return tree;
  }

  /**
   * Flattens the tree by pushing down the field name. For example, if
   * the tree looks like this:
   * <pre>
   *                EQ
   *             /      \
   *        VALUE         EQ
   *        /    \      /    \
   *      TEXT  field GLOBAL (N)
   * </pre>
   * Then we will output tree that looks like this:
   * <pre>
   *                EQ
   *             /      \
   *        VALUE       (N)
   *        /    \
   *      TEXT  field
   * </pre>
   * Here <code>(N)</code> is an arbitrary node. We also drop EQ if it
   * is in front of conjunction or disjunction. We do not drop it for
   * other comparators, as we want parsing to fail for foo &lt; (1 2).
   */
  private static Tree flatten(Tree tree, Tree restriction) throws QueryTreeException {
    if (tree.getType() == QueryLexer.VALUE) {
      return tree;
    }
    if (tree.getType() == QueryLexer.HAS || tree.getType() == QueryLexer.EQ) {
      Tree lhs = tree.getChild(0);
      if (lhs.getType() == QueryLexer.VALUE) {
        String myField = lhs.getChild(1).getText();
        if (restriction == null) {
          restriction = lhs;
        } else {
          String otherField = restriction.getChild(1).getText();
          if (!myField.equals(otherField)) {
            throw new QueryTreeException(
                String.format("Restriction on %s and %s", otherField, myField),
                lhs.getChild(1).getCharPositionInLine());
          }
        }
      }
      Tree rhs = tree.getChild(1);
      Tree flattened = flatten(rhs, restriction);
      if (flattened.getType() == QueryLexer.HAS
          || flattened.getType() == QueryLexer.EQ
          || flattened.getType() == QueryLexer.CONJUNCTION
          || flattened.getType() == QueryLexer.DISJUNCTION
          || flattened.getType() == QueryLexer.SEQUENCE) {
        return flattened;
      }
      if (flattened != rhs) {
        tree.setChild(1, flattened);
      }
      if (restriction != lhs) {
        tree.setChild(0, restriction);
      }
      return tree;
    }
    for (int i = 0; i < tree.getChildCount(); ++i) {
      Tree original = tree.getChild(i);
      Tree flattened = flatten(tree.getChild(i), restriction);
      if (original != flattened) {
        tree.setChild(i, flattened);
      }
    }
    return tree;
  }
}
