import sys, random, math, time
import numpy as np
from matplotlib import pyplot as plt
from copy import copy, deepcopy

def restore_infeasible_solutions(population, population_size, graph_matrix, no_of_nodes):
    def check_feasiblity(solution, graph_matrix, no_of_nodes):
        solution = list(solution)
        reachable = deepcopy(graph_matrix)
        false_row = [False]*no_of_nodes
        for i in range(no_of_nodes):
            if solution[i] == '0':
                continue
            for j in range(no_of_nodes):
                if graph_matrix[i][j]:
                    reachable[j] = false_row
                    reachable[:, j] = [False]

        return reachable.any() == False

    for k in range(population_size):
        solution = population[k]
        # if solution is feasible
        while (not check_feasiblity(solution, graph_matrix, no_of_nodes)):
            # if solution is not feasible
            solution = list(solution)
            _, counts = np.unique(solution, return_counts=True) # counts zeros in the string
            flip_bit = int(counts[0]*np.random.uniform(low=0.0, high=1.0, size=1))
            for i in range(no_of_nodes):
                if (solution[i] == '0'):
                    flip_bit = flip_bit - 1
                if (flip_bit < 0):
                    solution[i] = '1'
                    break
            population[k] = ''.join(solution)

    return population

def update_fitness(population, population_size, node_weights, no_of_nodes, gen_worst_solution):
    # initialize fitness values
    fitness = []
    for i in range(population_size):
        solution = list(population[i])
        solution_cost = 0
        for j in range(no_of_nodes):
            if(solution[j] == '1'):
                solution_cost += node_weights[j]
        fitness.append((1+gen_worst_solution-solution_cost)**2)

    return fitness

def create_mating_pool(population, population_size, fitness):
    mating_pool = []
    while(len(mating_pool) < population_size):
        candidate0 = int(np.random.uniform(low=0.0, high=1.0, size=1)*population_size)
        candidate1 = int(np.random.uniform(low=0.0, high=1.0, size=1)*population_size)
        if(fitness[candidate0] > fitness[candidate1]):
            mating_pool.append(population[candidate0])
        else:
            mating_pool.append(population[candidate1])
    return mating_pool

def apply_crossoveer(population, population_size, crossover_prob, no_of_nodes):
    for i in range(0,population_size-1,2):
        crossover_point = int(no_of_nodes*np.random.uniform(low=0.0, high=1.0, size=1))
        if (crossover_prob < np.random.uniform(low=0.0, high=1.0, size=1)):
            continue
        # apply crossover
        population[i], population[i+1] = population[i+1][0:crossover_point] + population[i][crossover_point:], \
                                        population[i][0:crossover_point] + population[i+1][crossover_point:],
    return population

def apply_mutation(population, population_size, mutation_prob, no_of_nodes):
    for i in range(population_size):
        solution = list(population[i])
        for j in range(no_of_nodes):
            if (mutation_prob < np.random.uniform(low=0.0, high=1.0, size=1)):
                continue
            if solution[j] == '0':
                solution[j] = '1'
            else:
                solution[j] = '0'
        population[i] = ''.join(solution)
    return population

def analyze_population(population, population_size, node_weights, best_solution):
    total = 0
    gen_best_solution, gen_worst_solution = math.inf, 0
    for solution in population:
        candidate_solution = 0
        for i in range(no_of_nodes):
            if(solution[i] == '1'):
                candidate_solution += node_weights[i]
        
        total += candidate_solution
        # update the best solution if candidate is better
        if(candidate_solution < best_solution):
            best_solution = candidate_solution
            gen_best_solution = candidate_solution
        # update the generation's best solution
        elif(candidate_solution < gen_best_solution):
            gen_best_solution = candidate_solution
        # update the worst solution if candidate is worse
        elif(candidate_solution > gen_worst_solution):
            gen_worst_solution = candidate_solution

    average_solution = total / population_size
    return best_solution, average_solution, gen_worst_solution

# start time
start_time = time.time()

# get command line arguments
args = sys.argv[1:]
file_name = args[0]
no_of_generations = int(args[1])
population_size = int(args[2])
crossover_prob = float(args[3])
mutation_prob = float(args[4])

# print all the arguments
print("\n================== Arguments ===================")
print("file name:               ", file_name)
print("number of generations:   ", no_of_generations)
print("population size:         ", population_size)
print("crossover probability:   ", crossover_prob)
print("mutation probability:    ", mutation_prob)
print("================================================\n")

no_of_nodes = 0
no_of_edges = 0
node_weights = []
graph_matrix = [[]]
best_solution = math.inf
average_solutions = []

# read file
with open(file_name) as fp:
        no_of_nodes = int(fp.readline().strip())
        no_of_edges = int(float(fp.readline().strip()))
        graph_matrix = np.zeros(shape=(no_of_nodes, no_of_nodes), dtype=bool)

        # get node weights
        for i in range(no_of_nodes):
            node_weights.append(float((fp.readline().strip().split(' ')[1]).replace(',','.')))

        # get edges
        line = fp.readline()
        while line:
            nodes = line.strip().split(' ')
            graph_matrix[int(nodes[0])][int(nodes[0])] = True
            graph_matrix[int(nodes[0])][int(nodes[1])] = True
            line = fp.readline()

# print all the arguments
print("================== Graph Info ==================")
print("number of nodes:                 ", no_of_nodes)
print("number of edges:                 ", no_of_edges)
#print("node weigths:                    ", node_weights)
#print("graph_matrix:                    \n", graph_matrix)
print("================================================\n")

# create initial population with uniform randoms
population, fitness = [], []
for i in range(population_size):
    random_solution = ''.join(['1' if k>=0.5 else '0' for k in np.random.uniform(low=0.0, high=1.0, size=no_of_nodes)])
    population.append(random_solution)

# Start genetic algorithm
for i in range(no_of_generations):
    # restore infeasible solutions
    population = restore_infeasible_solutions(population, population_size, graph_matrix, no_of_nodes)

    # update best solution
    best_solution, average_solution, gen_worst_solution = analyze_population(population, population_size, node_weights, best_solution)
    average_solutions.append(average_solution)
    print("Generation: {}, Average Solution: {}, Best Solution: {}".format(i, average_solution, best_solution))

    # update fitness values
    fitness = update_fitness(population, population_size, node_weights, no_of_nodes, gen_worst_solution)
   
    # create mating pool
    population = create_mating_pool(population, population_size, fitness)

    # shuffle the population
    population = random.sample(population, population_size)

    # apply crossover
    population = apply_crossoveer(population, population_size, crossover_prob, no_of_nodes)

    # apply mutation
    population = apply_mutation(population, population_size, mutation_prob, no_of_nodes)

# Create graph
#print("========= Average Solutions =========")
#print(average_solutions)
plt.plot(np.arange(0, no_of_generations), average_solutions)
plt.title("Average Fitness Graph")
plt.xlabel("Generations")
plt.ylabel("Average Fitness")
#plt.show()
fig_name = '_'.join([file_name, str(no_of_generations), str(population_size), str(crossover_prob), str(mutation_prob)])
print(fig_name)
plt.savefig(fig_name+'.png')

# end time
end_time = time.time()
print('Execution time = ', (end_time - start_time)/60)
