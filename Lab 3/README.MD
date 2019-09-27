# Simulated Annealing
SimulatedAnnealing.java works on the same problem as lab 2 (ie reducing cost of interconnections between graph vertices/components sorted into 2 bins), however, instead of swapping components randomly between bins (greedy approach), but instead use probability to assist in choosing the probable best generation. This model updates based on the following structure:

if costChange < 0 [Compare the cost, keep any better (or probabilistically better) results]
then bestCost ← newCost;
else if costChange > 0 && rand3 < e^-(costChange/control)
then bestCost ← newCost;
else
then swapBins(bins, rand1, rand2); [Swap bins back]
