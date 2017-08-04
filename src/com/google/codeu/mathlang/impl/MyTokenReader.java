// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.mathlang.impl;


import java.io.IOException;
import java.lang.NumberFormatException;

import java.lang.StringBuilder;

import com.google.codeu.mathlang.core.tokens.Token;
import com.google.codeu.mathlang.core.tokens.NameToken;
import com.google.codeu.mathlang.core.tokens.SymbolToken;
import com.google.codeu.mathlang.core.tokens.NumberToken;
import com.google.codeu.mathlang.core.tokens.StringToken;

import com.google.codeu.mathlang.parsing.TokenReader;

// MY TOKEN READER
public final class MyTokenReader implements TokenReader {
  private StringBuilder currentToken = new StringBuilder();
  private int currentPos = 0;
  private String source;

  public MyTokenReader(String source) {
    this.source = source;
  }

  @Override
  public Token next() throws IOException {
    while(remaining() > 0 && Character.isWhitespace(peek())) {
      read();
    }

    if (remaining() <= 0) {
      return null;
    }

    //sees if it starts with quotes
    else if (peek() == '"') {
      return readWithQuotes();
    }

    else {
      return tokenType(readWithNoQuotes());
    }
  }

  //determine how man chars are left
  private int remaining() {
    return source.length() - currentPos;
  }

  //look at the char at the current position
  private char peek() throws IOException {
    if(currentPos < source.length()) {
      return source.charAt(currentPos);
    } else {
      throw new IOException("Error in the peek function.");
    }
  }

  //read the char at the current position
  private char read() throws IOException {
    final char c = peek();
    currentPos += 1;
    return c;
  }

  //read text with quotes
  //same as in Tokenizer.java
  private Token readWithQuotes() throws IOException {
    currentToken.setLength(0);
    if(read() != '"') {
      throw new IOException("Strings must start with opening quote");
    }
    while(peek() != '=') {
      currentToken.append(read());
    }
    read();
    return currentToken.toString();

  //read text with no quotes
  //same as in Tokenizer.java
  private String readWithNoQuotes() throws IOException {
    currentToken.setLength(0);
  	while(remaining() > 0 && !Character.isWhitespace(peek())) {
  	  currentToken.append(read());
  	}
  	return currentToken.toString();
  }

  //determines what kind of token using a switch statement
  private Token tokenType(String content) throws IOException{
	  if(content.length() == 1) {
		  switch(content.charAt(0)) {
        case ';': return new SymbolToken(';');
        case '=': return new SymbolToken('=');
        case '+': return new SymbolToken('+');
        case '-': return new SymbolToken('-');
        case '\"': return new StringToken(readString(currentChar));
        case '\0': return null;

		  	default:
          //letter
		  		if(Character.isLetter(content.charAt(0))) {
		  			return new NameToken(content);
		  		}
          //number
          else if (Character.isDigit(content.charAt(0))) {
		  			return new NumberToken(Double.parseDouble(content));
		  		}
          else {
		  			throw new IOException("Unknown character.");
		  		}
		  }
		//name tokens
	  } else if (content.equals("note") || content.equals("print") || content.equals("let")) {
		  NameToken returnNameToken = new NameToken(content);
		  return returnNameToken;

		//string tokens
	  } else {
		  try {
			  Double.parseDouble(content);
			  return new NumberToken(Double.parseDouble(content));
		  } catch(NumberFormatException e) {
			  return new StringToken(content);
		  }
	  }
  }
}
