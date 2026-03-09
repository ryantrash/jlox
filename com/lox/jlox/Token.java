package com.lox.jlox; 

class Token {
    final TokenType type; 
    final String lexeme; 
    final Object literal; 
    final int line; 


    /**
     * A single Lox token
     * @param type The type of the token as defined in {@link TokenType#TokenType()}
     * @param lexeme The text of the lexeme given
     * @param literal The literal value of the object
     */
    Token(TokenType type, String lexeme, Object literal, int line){
        this.type = type; 
        this.lexeme = lexeme; 
        this.literal = literal; 
        this.line = line; 
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal + " " + line; 
    }
}