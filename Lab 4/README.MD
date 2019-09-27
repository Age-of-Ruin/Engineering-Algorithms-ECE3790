# Genetic Algorithm
GeneticAlgorithm.java implements the same interconnect minimization problem but utilizes genetic algorithms (namely tournament selection and roulette selection) to perturbe the system.

Tournament Selection works by randomly choosing 2 nodes in the population then taking the node with greater fitness. This repeats for every unique pair of nodes. The fittest 1/2 of the population is kept for the next iteration.

Roulette Selection works by assigning a probability of selection to each node based on it's fitness divided by the sum fitness of all nodes. It then picks 1/2 of the current population (using these probabilites) to be kept for the next iteration.
