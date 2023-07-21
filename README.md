# Sudoku
Playable sudoku app with toggleable hints and some autosolving. Used to explore NP-complete problems.
Built with Java Swing.

![Screenshot of app with hints off](https://github.com/djlalo08/sudoku/blob/main/Screen%20Shot%202023-07-21%20at%2012.08.26%20PM.png?raw=true)
![Screenshot of app with hints on](https://github.com/djlalo08/sudoku/blob/main/Screen%20Shot%202023-07-21%20at%2012.07.48%20PM.png?raw=true)
## How to run

Navigate into the /bin directory and run: java Runner2

## Controls

Click to select a cell, or navigate to adjacent cells with WASD.
Type a number to input it into an empty cell.

Use the following keys:
- f: fill puzzle with numbers
- l: load in a preset puzzle
- n: new empty puzzle
- c: make a clone of existing puzzle (including user inputted numbers)
- h: toggle hints
- -: enable 'minus' mode
- e: solve next available
- t: solve triples
