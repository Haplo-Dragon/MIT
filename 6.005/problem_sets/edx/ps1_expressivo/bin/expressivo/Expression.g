/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

// grammar Expression;

/*
 *
 * You should make sure you have one rule that describes the entire input.
 * This is the "start rule". Below, "root" is the start rule.
 *
 * For more information, see the parsers reading.
 */

@skip whitespace{
	// A root is a sum.
	root ::= sum;

	// A sum is a product, optionally followed by '+' and more products.
	// This allows us to implement order of operations, so that products always precede
	// sums (that is, products are LOWER than sums in the generated tree).
	sum ::= product ('+' product)*;

	// A product is a primitive, optionally followed by '*' and more primitives
	product ::= primitive ('*' primitive)*;

	// A primitive is a number, a variable, a sum, or a product.
	primitive ::= number | variable | '(' sum ')' | '(' product ')';

}

// A number is numerals, possibly followed by a decimal and more numerals.
number ::= ([0-9]+)*('.'[0-9]+)*;

// A variable is an optional coefficient, followed by an alphabetic name and an optional
// exponent.
variable ::= (number)*[A-Za-z]+('^'[0-9]+)*;

// Whitespace is literal space characters or tabs, returns, or newlines.
whitespace ::= [ \t\r\n]+;
