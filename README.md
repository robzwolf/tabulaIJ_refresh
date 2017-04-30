# Tabula
Tabula is a board game that was played by the Romans.

# About this Project
This repo replaces the previous (broken...) [tabula](https://github.com/robzwolf/tabula/) repo.
This project is my end-of-year assignment for one of my first year modules, Introduction to Programming.

# Instructions
## How to Compile
1) Navigate to the `submit` directory containing the `.java` files using `cd`.
2) Run `javac -cp ".;gson-2.8.0.jar" Game.java` if using a Windows host, otherwise `javac -cp ".:gson-2.8.0.jar" Game.java` for Unix-based operating systems.

## How to Run
1) Make sure you have compiled (see above).
2) Navigate to the `submit` directory containing the `.java` files using `cd`.
3) Run `java -cp ".;gson-2.8.0.jar" Game` if using a Windows host, otherwise `java -cp ".:gson-2.8.0.jar" Game` for Unix-based operating systems.

## Modification
Edit any of the following constants in the interfaces to alter gameplay.

- `BoardInterface.PIECES_PER_PLAYER`
- `BoardInterface.NUMBER_OF_LOCATIONS`
- `DieInterface.NUMBER_OF_SIDES_ON_DIE`

If changing the `Colour` class, the colour that is listed first will be used to start the game (the starting colour is selected by `Colour.values()[0]`).

# Shameless Plug
Visit my website at [robbie.xyz](http://robbie.xyz).
