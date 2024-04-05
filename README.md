# assignment-three-cop4520

## Compilation and Execution
```bash 
  javac MinotaurBirthdayParty.java
  java MinotaurBirthdayParty
```

```bash 
  javac MarsRover.java
  java MarsRover
```

## Efficiency
MarsRover: The program efficiently utilizes concurrency and synchronization to collect temperature readings from multiple sensors concurrently and process them in a thread-safe manner.<br>
MinotaurBirthdayParty: 
The program efficiently manages concurrent operations on a linked list of presents, ensuring thread safety with the use of locks. It distributes tasks randomly among multiple threads, effectively utilizing parallel technques. The choice of data structures like a linked list and a concurrent queue enhances efficiency in handling the present addition and removal, as well as thanking the guests.

## Proof of Correctness
To ensure program accuracy, print statements to the console are happening at each important step. I ensure that each thread did in fact accomplish its task before program termination in both files.



