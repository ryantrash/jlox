package com.lox.jlox;

import static com.lox.jlox.TokenType.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Scanner {

    private final String source;
    /**
     * The List containing all raw tokens in the source code
     */
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final HashMap<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    /**
     * Scans all tokens in the source code, adding an EOF token at the end
     *
     * @return tokens - a list object of all tokens
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    /**
     * Scans a singular token and called addToken based on the input
     */
    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(' ->
                addToken(LEFT_PAREN);
            case ')' ->
                addToken(RIGHT_PAREN);
            case '{' ->
                addToken(LEFT_BRACE);
            case '}' ->
                addToken(RIGHT_BRACE);
            case '?' -> 
                addToken(QUESTION);
            case ':' ->
                addToken(COLON); 
            case ',' ->
                addToken(COMMA);
            case '.' ->
                addToken(DOT);
            case '-' ->
                addToken(MINUS);
            case '+' ->
                addToken(PLUS);
            case ';' ->
                addToken(SEMICOLON);
            case '*' ->
                addToken(STAR);
            case '!' ->
                addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' ->
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' ->
                addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' ->
                addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if (match('*')) {
                    System.out.println("Block"); 
                    blockComment();
                } else {
                    addToken(SLASH);
                }
                break;
            }

            case ' ', '\r', '\t' -> {
                //do nothing on whitespace
            }
            case '\n' ->
                line++;

            case '"' ->
                string();

            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    indentifier();
                } else {
                    Lox.error(line, "Unexpected character");
                }
            }
        }
    }

    private void indentifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);

        if (type == null) {
            type = INDENTIFIER;
        }
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(NUMBER, Double.valueOf(source.substring(start, current)));
    }

    /**
     * Handles string literals by iterating until the next " is found. Allows
     * for multiline strings.
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }

            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated String");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }


    private void blockComment(){
        while(!(peek() == '*' && peekNext() == '/')){
            if(peek() == '\n'){
                line++; 
            }

            advance();  
        }

        if(isAtEnd()) {
            Lox.error(line, "Unterminated Block Comment");
        }

        advance(); advance(); 
    }


    /**
     * A conditional advance function. Consumes a character if said character
     * matches param expected, incrementing one up and returning true.
     *
     * @param expected The expected char to be consumed
     * @return True if the next char matches param expected.
     */
    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }

        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Advances one char by incrementing the current index
     *
     * @return The char at the next index (given by current++)
     */
    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

}
