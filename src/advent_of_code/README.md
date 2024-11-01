# Breakdown of Files

Jump to day: [1](#day01clj)&nbsp;|&nbsp;[2](#day02clj)&nbsp;|&nbsp;[3](#day03clj)&nbsp;|&nbsp;[4](#day04clj)&nbsp;|&nbsp;[5](#day05clj)&nbsp;|&nbsp;[6](#day06clj)&nbsp;|&nbsp;[7](#day07clj)&nbsp;|&nbsp;[8](#day08clj)&nbsp;|&nbsp;[9](#day09clj)&nbsp;|&nbsp;[10](#day10clj)&nbsp;|&nbsp;[11](#day11clj)&nbsp;|&nbsp;[12](#day12clj)&nbsp;|&nbsp;[13](#day13clj)&nbsp;|&nbsp;[14](#day14clj)&nbsp;|&nbsp;[15](#day15clj)&nbsp;|&nbsp;[16](#day16clj)&nbsp;|&nbsp;[17](#day17clj)&nbsp;|&nbsp;[18](#day18clj)&nbsp;|&nbsp;[19](#day19clj)&nbsp;|&nbsp;[20](#day20clj)&nbsp;|&nbsp;[21](#day21clj)&nbsp;|&nbsp;[22](#day22clj)&nbsp;|&nbsp;[23](#day23clj)&nbsp;|&nbsp;[24](#day24clj)&nbsp;|&nbsp;[25](#day25clj)

Here is a breakdown of the various files in this directory. Files with names of
the form `dayNN.clj` represent the code actually used to solve the problems
(with some tweaking done using a static analysis plug-in for Leiningen). Files
with `bis` in the name are modified/tuned versions of the given original day.
(If you see comments in a file, I can usually promise you they were added after
the fact.)

The numbers in parentheses in the descriptions of the files represent the rank
I had for when my solutions were submitted and accepted. Time, if given, is a
rough estimate of how long it took to solve both halves.

A given day and part can be run via:

```
lein run DAY PART
```

where `DAY` is a number from 1-25 and `PART` is 1 or 2. If there is a "bis"
version of a day, that can be run via:

```
lein run -b DAY PART
```

## [day01.clj](day01.clj)

Day 1 (--/--).

Simple tracing of a path. Derived `manhattan-dist` and `first-duplicate`
routines from this, that were added to `utils.clj`.

## [day02.clj](day02.clj)

Day 2 (--/--).

Moving around a "keypad". Similar to many other move-around-a-field puzzles.
Found that I don't need to be testing numerical indices for overflow/underflow,
the `get-in` primitive will return `nil` when an index is out of bounds. This
may lead to a more-general function for handling movement in a field.

## [day03.clj](day03.clj)

Day 3 (--/--).

Part 1 was just validating triples of numbers as valid triangles. Part 2
required taking every three rows of input and reading the numbers in columns to
get the corresponding 3 triangle values. Here, `partition` and the `transpose`
function in utils were all that was needed to transform the data.

## [day04.clj](day04.clj)

Day 4 (--/--).

String parsing, pseudo-decryption. Nothing really novel in this day, other than
use of `comp` and `partial`. To get the answer to part 2, one had to manually
examine the search results.

## [day05.clj](day05.clj)

Day 5 (--/--).

This lead to adding a `md5` function to utils, and playing around with lazy
infinite sequences.

## [day06.clj](day06.clj)

Day 6 (--/--).

Not a lot of novel content in this one. It proved rather easy to take the list
of lines, apply `u/transpose`, then do a count by character. The sorting of
the frequency count was done and the answers derived from that.

## [day07.clj](day07.clj)

Day 7 (--/--).

Interesting puzzles. Was unable to reuse any code between parts 1 and 2 due to
the way I did part 1. But in hindsight, I could re-do part 1 so that part 2
could reuse most of it. Started using `reduce`+`reduced` a lot more.

## [day08.clj](day08.clj)

Day 8 (--/--).

Took an inordinate amount of time. The puzzle wasn't that hard, the lost time
was mostly due to spacing on how to effectly "rotate" a vector and a case
where a vector was being converted to a list without my understanding.

From this I added two new utils: `create-field` and `display`. The first is
used for creating a "field" (matrix) of characters from input lines or from a
single specified character. The second displays a matrix of characters as
though on a display screen.

## [day09.clj](day09.clj)

Day 9 (--/--).

This one proved tricky. While part 1 went fairly smoothly with a recursive
solution that built the entire decompressed string, part 2 would decompress
well beyond the capacity of the process to store.

The solution was quite simple once it occurred to me, and I think I could come
up with a function that would solve both parts if I have time to try.

No new utils code came out this one.

## [day10.clj](day10.clj)

Day 10 (--/--).

This was a circuit-simulation kind of problem. The first part was to just
identify which "bot" would be the one to compare two specific types of "chips".
The second part involved running the system until its end and calculating the
product of values of one "chip" from each of the output bins 0, 1, and 2.

Not sure if I can pull out any of this code for utils. The techniques were
valuable, but the code is very puzzle-specific.

## [day11.clj](day11.clj)

Day 11 (--/--).

This was a search problem, solved by BFS. Not solved very well, though, as part
1 took ~22 seconds and part 2 took over 25 minutes. Might be worth revisiting
and trying an A* approach.

## [day12.clj](day12.clj)

Day 12 (--/--).

This was an emulate-a-machine problem. Very simple, including part 2. Used the
opportunity to get more comfortable with using keywords for this class of
puzzle.

## [day13.clj](day13.clj)

Day 13 (--/--).

Interesting puzzles. Part 1 was a simple maze-walking task with the twist that
the maze was infinite and the wall/open status of each coordinate was a function
of an algebraic equation and counting the bits of the base-2 representation of
the value. This was handled by making the calculation function memoized.

Part 2 was a sort of "flood-fill" puzzle, asking for how many open spaces can
be reached within a given number of steps. Some small modifications to the
search function were all that was needed.

## [day14.clj](day14.clj)

Day 14 (--/--).

For this puzzle, I focused on tying together lazy and/or infinite sequences.
It required re-using the MD5 function added earlier, and part 2 took 139s to
run, compared to 280ms for part 1.

## [day15.clj](day15.clj)

Day 15 (--/--).

Surprisingly easy. Just a matter of lining up numbers with modulos such that
each modulus came out to 0. Used an infinte sequence along with `filter` to
iterate from 0 until something matched. Part 1 finished in 29ms, with part 2
taking just 6s. There was probably a more mathematical approach that would have
been even faster, but this worked.

## [day16.clj](day16.clj)

Day 16 (--/--).

Another easy one, due mostly to the way Clojure can handle operations on lists.
Step 1 of the puzzle was to take the input and expand it to a given minimum
length (using a modified Dragon Curve algorithm). Step 2 was to calculate a
checksum value on the resulting data. The checksum was the easier of the steps,
using a map structure to connect 0/1 pairs to a checksum digit and using a `for`
comprehension to apply this.

Part 1 ran in 4.7ms. Part 2 ran (essentially brute-force) with no modifications
and took 115.4s. I suspect that there could have been a solution that pipelined
the generation of the fill-data as well as the calculation of the checksum, but
the brute-forcing was enough for this one.

## [day17.clj](day17.clj)

Day 17 (--/--).

Another search problem, solvable by BFS. This leads to wondering what it would
take to create a generalized BFS function. This puzzle's twist was that there
were essentially no walls, just doors. And whether given doors were locked or
open was a function of an MD5 hash. There really must be a way to generalize
this.

## [day18.clj](day18.clj)

Day 18 (--/--).

This was another fairly easy one. Used `iterate` and `take` to get the rows
needed, then `flatten` and `frequencies` to get the count of safe tiles. Part 1
took 23.2ms, but part 2 took 129.9s. This doesn't reflect the scaling of part
2, so this may be worth looking at. Possibly make more of a pipeline of lazy
sequences.

## [day19.clj](day19.clj)

Day 19 (--/--).

This was rough. Part 1 went easily enough, part 2's initial solution ran for
over 2 hours without ending. Tried to math it out, in the end I had to adapt a
solution from reddit. The solution was embarassingly simple.

## [day20.clj](day20.clj)

Day 20 (--/--).

Searching through ranges. Solutions performed well as-is, no optimizations
needed. Added a new util function for parsing number ranges.

## [day21.clj](day21.clj)

Day 21 (--/--).

The input was a set of "instructions" for scrambling a string into a "password".
Part 1 was straightforward and ran pretty quickly. Part 2 was to reverse the
process and determine what plaintext corresponded to a given password. All but
one of the instructions were easily reversed, but I just couldn't get part 2
to work, due to the remaining instruction. In the end, just brute-forced the
answer by scrambling ALL permutations until one produced the desired password.

## [day22.clj](day22.clj)

Day 22 (--/--).

This day's puzzle yielded my first wrong submissions (for part 2). Part 1 was
rather straightforward with a function from the combinatorics library for
generating the pairs.

Part 2, though, was rough. Looking on reddit, almost everyone did it visually
by hand after printing the "map". I tried, but I did the math wrong and got
two wrong answers before getting it right. It might be automatically-solvable
with some use of A* and special cases, but not right now.

## [day23.clj](day23.clj)

Day 23 (--/--).

A return to the pseudo-assembly of day 12. Adds a new instruction, and part 2
"hints" at a need to hand-optimize part of the input program.

## [day24.clj](day24.clj)

Day 24 (--/--).

This puzzle combined a Traveling Salesman Problem with a need for BFS. This
would the third (fourth?) day out 24 that required BFS, so I will definitely be
taking some time to generalize that code.

For the TSP part, though, rather than integrate the TSP code I wrote for the
2015 run ([see here](https://github.com/rjray/notebook/blob/main/writing/clojure-tsp.md)),
I used the same brute-force method I had intially used there (permutations of
all possible ordering of the edges). I will go back and try to fit a "proper"
TSP algorithm to it, but the brute-force ran in under 1s for both parts.

## [day25.clj](day25.clj)

Day 25 (--/--).
