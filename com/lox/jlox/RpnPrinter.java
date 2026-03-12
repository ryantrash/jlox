package com.lox.jlox;

class RpnPrinter implements Expr.Visitor<String> {

    public String print(Expr expr) {
        return expr.accept(this);
    }

    public static void main(String[] args) {
        RpnPrinter printer = new RpnPrinter();

        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)
                )
        );

        System.out.println(printer.print(expression));
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return expr.left.accept(this) + " " + expr.right.accept(this) + " " + expr.operator.lexeme; 
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);  
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value.toString(); 
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.right.accept(this) + " " + expr.operator.lexeme; 
    }

    @Override
    public String visitTernaryExpr(Expr.Ternary expr){
        return expr.eval.accept(this) + " " + expr.left.accept(this) + " " + expr.right.accept(this) + " IF "; 
    }
}
