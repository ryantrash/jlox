package com.lox.jlox; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths; 
import java.util.List; 

/**
 * The Lox language intepreter.
 */
public class Lox {
    /**
     * Boolean to detect when an error has been raised.
     */
    static boolean hadError = false; 
    public static void main(String[] args) throws IOException{
        // Ensure only 1 or 0 files have been attached
        if(args.length > 1){
            System.out.println("Usage: jlox [script]"); 
            System.exit(64);
        } else if(args.length == 1){
            runFile(args[0]); 
        } else {
            runPrompt(); 
        }
    }
    /**
     * Runs a file from a path string in lox
     * @param path The string of the file to run
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path)); 

        run(new String(bytes, Charset.defaultCharset())); 

        if(hadError) System.exit(65); 
    }
    /**
     * Runs Lox in the sysout
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in); 
        BufferedReader reader = new BufferedReader(input); 

        for(;;){
            System.out.print("> "); 
            String line = reader.readLine(); 
            
            if(line == null) break;

            run(line); 

            // Turn haderror to false to not halt user terminal session
            hadError = false; 
        }
    }

    /**
     * Runs Lox source code token by token
     * @param source The source code to be run
     */
    private static void run(String source){
        Scanner scanner = new Scanner(source); 

        List<Token> tokens = scanner.scanTokens(); 

        Parser parser = new Parser(tokens); 
        Expr expression = parser.parse(); 

        if(hadError) return; 

        System.out.println(new AstPrinter().print(expression)); 
    }

    /**
     * Reports an error
     * @param line The line on which the error occured
     * @param message The accompanying error message
     */
    static void error(int line, String message){
        report(line, "", message); 
    }
    
    /**
     * The output function for errors caused in Lox. 
     * @param line The line number at which the error occured
     * @param where The raw string of the line at which the error occured
     * @param message the accompanying error message
     */
    private static void report(int line, String where, String message){
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true; 
    }

    static void error(Token token, String message){
        if(token.type == TokenType.EOF){
            report(token.line, " at end", message); 
        } else {
            report(token.line, " at '" + token.lexeme + "'", message); 
        }
    }
}