import numpy as np
import queue as q

class Node:
    def __init__(self, x, y, parent, cost, node_type, depth, heuristic_cost):
        self.x = x                              # row 
        self.y = y                              # column
        self.parent = parent                    # parent of the node
        self.cost = cost                        # real cost from root to this node
        self.node_type = node_type              # normal, trap, goal or start
        self.depth = depth
        self.heuristic_cost = heuristic_cost    # city block distance to the closest goal state

def expand_frontier(node, matrix, frontier, expand_sequence, explored, search_type, heuristic_func):
    expand_sequence.append(node)
    explored.append((node.x, node.y))
    
    order_list = [3,4,1,2]  # frontier expand order 
    boolean_list = [True, True, True, True] # valid move list

    # expand frontier if goal state is not reached
    cell = matrix[node.x][node.y]

    if ((node.x-1, node.y) in explored):
        boolean_list[3] = False
    if ((node.x+1, node.y) in explored):
        boolean_list[1] = False
    if ((node.x, node.y+1) in explored):
        boolean_list[0] = False
    if ((node.x, node.y-1) in explored):
        boolean_list[2] = False
    
    # if the node in frontier is reachable from another parent, with less cost
    if(search_type == 'A*' or search_type == 'UCS'):
        j = 0
        while j < len(frontier):
            nodes = frontier[j]
            cost = nodes.cost
            step_cost = 7 if node.node_type == 't' else 1
            new_cost = step_cost + node.cost
            if(nodes.x == node.x-1 and nodes.y == node.y):
                if(new_cost < cost):
                    del frontier[j]
            if(nodes.x == node.x-1 and nodes.y == node.y):
                if(new_cost < cost):
                    del frontier[j]
            if(nodes.x == node.x-1 and nodes.y == node.y):
                if(new_cost < cost):
                    del frontier[j]
            if(nodes.x == node.x-1 and nodes.y == node.y):
                if(new_cost < cost):
                    del frontier[j]
            j+=1

    # check valid moves
    for nodes in frontier:

        if (boolean_list[3] == True and nodes.x == node.x-1 and nodes.y == node.y):
            boolean_list[3] = False
        elif (boolean_list[1] == True and nodes.x == node.x+1 and nodes.y == node.y):
            boolean_list[1] = False
        elif (boolean_list[0] == True and nodes.x == node.x and nodes.y == node.y+1):
            boolean_list[0] = False
        elif (boolean_list[2] == True and nodes.x == node.x and nodes.y == node.y-1):
            boolean_list[2] = False

    # reverse the expanding order
    if search_type == 'DFS' or search_type == 'IDP':
        order_list = order_list[::-1]
        boolean_list = boolean_list[::-1]    
    
        # DFS & IDP = north,    other = east
    if(cell[order_list[0]] == 'T' and boolean_list[0] == True):   
            step_cost = 0
            new_node_x, new_node_y = node.x, node.y+1

            if search_type == 'DFS' or search_type == 'IDP':
                new_node_x, new_node_y = node.x-1, node.y

            node_type = matrix[new_node_x][new_node_y][0]
            if(node_type == 'n' or node_type == 'g'):
                step_cost = 1
            else:
                step_cost = 7
            node_move = Node(new_node_x, new_node_y, node, node.cost+step_cost, node_type, node.depth+1, heuristic_func[new_node_x][new_node_y])
            frontier.append(node_move)    

        # DFS & IDP = west,    other = south
    if(cell[order_list[1]] == 'T' and boolean_list[1] == True):   
        step_cost = 0
        new_node_x, new_node_y = node.x+1, node.y

        if search_type == 'DFS' or search_type == 'IDP':
            new_node_x, new_node_y = node.x, node.y-1

        node_type = matrix[new_node_x][new_node_y][0]
        if(node_type == 'n' or node_type == 'g'):
            step_cost = 1
        else:
            step_cost = 7
        node_move = Node(new_node_x, new_node_y, node, node.cost+step_cost, node_type, node.depth+1, heuristic_func[new_node_x][new_node_y])
        frontier.append(node_move)

        # DFS & IDP = south,    other = west
    if(cell[order_list[2]] == 'T' and boolean_list[2] == True):   
        step_cost = 0
        new_node_x, new_node_y = node.x, node.y-1

        if search_type == 'DFS' or search_type == 'IDP':
            new_node_x, new_node_y = node.x+1, node.y

        node_type = matrix[new_node_x][new_node_y][0]
        if(node_type == 'n' or node_type == 'g'):
            step_cost = 1
        else:
            step_cost = 7
        node_move = Node(new_node_x, new_node_y, node, node.cost+step_cost, node_type, node.depth+1, heuristic_func[new_node_x][new_node_y])
        frontier.append(node_move)

        # DFS & IDP = east,    other = north
    if(cell[order_list[3]] == 'T' and boolean_list[3] == True):   
        step_cost = 0
        new_node_x, new_node_y = node.x-1, node.y

        if search_type == 'DFS' or search_type == 'IDP':
            new_node_x, new_node_y = node.x, node.y+1

        node_type = matrix[new_node_x][new_node_y][0]
        if(node_type == 'n' or node_type == 'g'):
            step_cost = 1
        else:
            step_cost = 7
        node_move = Node(new_node_x, new_node_y, node, node.cost+step_cost, node_type, node.depth+1, heuristic_func[new_node_x][new_node_y])
        frontier.append(node_move)

    
    return frontier, explored, expand_sequence

def graphSearch(matrix, root, search_type, iterative):
    frontier = []   # nodes to be expanded
    #state = [root.x, root.y]    # initial state

    expand_sequence = []    # list of the expanded nodes in order.
    expand_sequence.append(root)
   
    explored = []   # initialize the explored set to be empty
    explored.append((root.x, root.y))

    # initialize the frontier using initial state of the problem
    cell = matrix[root.x][root.y]

    if(cell[3] == 'T'):   # East
        step_cost = 0
        new_node_x, new_node_y = root.x, root.y+1
        node_type = matrix[new_node_x][new_node_y][0]
        if((new_node_x, new_node_y) not in explored):
            if(node_type == 'n' or node_type == 'g'):
                step_cost = 1
            else:
                step_cost = 7
            node = Node(new_node_x, new_node_y, root, root.cost+step_cost, node_type, root.depth+1, heuristic_func[new_node_x][new_node_y])
            frontier.append(node)

    if(cell[4] == 'T'):   # South
        step_cost = 0
        new_node_x, new_node_y = root.x+1, root.y
        node_type = matrix[new_node_x][new_node_y][0]
        if((new_node_x, new_node_y) not in explored):
            if(node_type == 'n' or node_type == 'g'):
                step_cost = 1
            else:
                step_cost = 7
            node = Node(new_node_x, new_node_y, root, root.cost+step_cost, node_type, root.depth+1, heuristic_func[new_node_x][new_node_y])
            frontier.append(node)

    if(cell[1] == 'T'):   # West
        step_cost = 0
        new_node_x, new_node_y = root.x, root.y-1
        if((new_node_x, new_node_y) not in explored):
            node_type = matrix[new_node_x][new_node_y][0]
            if(node_type == 'n' or node_type == 'g'):
                step_cost = 1
            else:
                step_cost = 7
            node = Node(new_node_x, new_node_y, root, root.cost+step_cost, node_type, root.depth+1, heuristic_func[new_node_x][new_node_y])
            frontier.append(node)

    if(cell[2] == 'T'):   # North
        step_cost = 0
        new_node_x, new_node_y = root.x-1, root.y
        if((new_node_x, new_node_y) not in explored):
            node_type = matrix[new_node_x][new_node_y][0]
            if(node_type == 'n' or node_type == 'g'):
                step_cost = 1
            else:
                step_cost = 7
            node = Node(new_node_x, new_node_y, root, root.cost+step_cost, node_type, root.depth+1, heuristic_func[new_node_x][new_node_y])
            frontier.append(node)
    
    if search_type == 'DFS' or search_type == 'IDP':
        frontier = frontier[::-1]
        
    while True:
        
        if(len(frontier) == 0):
            return "No Solution"

        if(search_type == 'DFS'):
            node = frontier[-1]
            frontier = frontier[:-1]

            if(node.node_type == 'g'):
                expand_sequence.append(node)
                return expand_sequence

            frontier, explored, expand_sequence = expand_frontier(node, matrix, frontier, expand_sequence, explored, search_type, heuristic_func)
            
        elif(search_type == 'BFS'):

            node = frontier[0]
            frontier = frontier[1:]

            if(node.node_type == 'g'):
                expand_sequence.append(node)
                return expand_sequence

            frontier, explored, expand_sequence = expand_frontier(node, matrix, frontier, expand_sequence, explored, search_type, heuristic_func)

        elif(search_type == 'IDP'):
            node = frontier[-1]
            frontier = frontier[:-1]

            if(node.node_type == 'g'):
                expand_sequence.append(node)
                return expand_sequence

            frontier, explored, expand_sequence = expand_frontier(node, matrix, frontier, expand_sequence, explored, search_type, heuristic_func)

            i = 0
            while( i < len(frontier)):
                if frontier[i].depth > iterative:
                    del frontier[i]

                i = i + 1

        elif(search_type == 'UCS'):
            i = 1
            min_index = 0
            min_cost = frontier[0].cost
            while i < len(frontier):
                if frontier[i].cost < min_cost:
                    min_cost = frontier[i].cost
                    min_index = i
                i = i + 1

            node = frontier[min_index]
            del frontier[min_index]

            if(node.node_type == 'g'):
                expand_sequence.append(node)
                return expand_sequence

            frontier, explored, expand_sequence = expand_frontier(node, matrix, frontier, expand_sequence, explored, search_type, heuristic_func)

        elif(search_type == 'GBFS'):
            i = 1
            min_index = 0
            min_cost = frontier[0].heuristic_cost
            while i < len(frontier):
                if frontier[i].heuristic_cost < min_cost:
                    min_cost = frontier[i].heuristic_cost
                    min_index = i
                i = i + 1

            node = frontier[min_index]
            del frontier[min_index]

            if(node.node_type == 'g'):
                expand_sequence.append(node)
                return expand_sequence

            frontier, explored, expand_sequence = expand_frontier(node, matrix, frontier, expand_sequence, explored, search_type, heuristic_func)

        elif(search_type == 'A*'):
            i = 1
            min_index = 0
            min_cost = frontier[0].heuristic_cost + frontier[0].cost
            while i < len(frontier):
                if frontier[i].heuristic_cost + frontier[i].cost < min_cost:
                    min_cost = frontier[i].heuristic_cost + frontier[i].cost
                    min_index = i
                i = i + 1

            node = frontier[min_index]
            del frontier[min_index]

            if(node.node_type == 'g'):
                expand_sequence.append(node)
                return expand_sequence

            frontier, explored, expand_sequence = expand_frontier(node, matrix, frontier, expand_sequence, explored, search_type, heuristic_func)

    for node in frontier:
        print("x:{}, y:{}".format(node.x, node.y))

def solution_path(expand_sequence):
    node = expand_sequence[-1]
    path = []
    while node != None:
        path.append(node)
        node = node.parent

    return path

def print_solution(expand_sequence, search_type):
    print("==============================================================")
    print("\t\t\tSearch method:", search_type)
    print("==============================================================")
    print("The cost of the solution:", expand_sequence[-1].cost)

    print("The solution path: ", end=" ")
    path = solution_path(expand_sequence)

    i=len(path) - 1
    while(i > 0):
        print("({},{})".format(path[i].x+1, path[i].y+1), end=" -> ")
        i = i - 1
    print("({},{})".format(path[0].x+1, path[0].y+1)) 

    print("The list of expanded nodes: ", end=" ")
    i=0
    while( i < len(expand_sequence) - 1 ):
        print("({},{})".format(expand_sequence[i].x+1, expand_sequence[i].y+1), end=", ")
        i = i + 1
    print("({},{})".format(expand_sequence[-1].x+1, expand_sequence[-1].y+1))
       
def create_heuristic_func(matrix, goals):
    number_of_rows, number_of_cols = len(matrix), len(matrix[0])
    heuristic_func = np.full((number_of_rows, number_of_cols), None)

    for i in range(number_of_rows):
        for j in range(number_of_cols):
            min_goal = number_of_rows + number_of_cols
            for goal in goals:
                cost = abs(goal[0]-i) + abs(goal[1]-j)
                if cost < min_goal:
                    min_goal = cost
            heuristic_func[i][j] = min_goal

    return heuristic_func

if __name__ == "__main__":
    matrix = [] # input matrix
    goals = []  # goal states
    start = []  # starting point
    filepath = 'input.txt'

    # read the input file
    with open(filepath) as fp:
        line = fp.readline()
        i = 0
        while line:
                #print("{}".format(line.strip()))
                words = line.split('_')
                words[-1] = words[7][:-1]
                matrix.append(words)
                i += 1
                line = fp.readline()
    
    # locate start and goal positions
    for i in range(len(matrix)):
        for j in range(len(matrix[0])):
            if matrix[i][j][0] == 's':
                start.append(i)
                start.append(j)
            elif matrix[i][j][0] == 'g':
                goals.append((i,j))

    # create and print the heuristic func
    heuristic_func = create_heuristic_func(matrix, goals)
    print("==============================================================")
    print("\t\t\tHeuristic Function")
    print("==============================================================")
    print(heuristic_func)
    print("")

    # create the root node
    root = Node(start[0], start[1], None, 0, 'n', 0, heuristic_func[start[0]][start[1]])
    
    # BFS
    expand_sequence = graphSearch(matrix, root, 'BFS', 0)
    print_solution(expand_sequence, "BFS")
    print("")
    
    # DFS
    expand_sequence = graphSearch(matrix, root, 'DFS', 0)
    print_solution(expand_sequence, "DFS")
    print("")

    # UCS
    expand_sequence = graphSearch(matrix, root, 'UCS', 0)
    print_solution(expand_sequence, "UCS")
    print("")

    # IDP
    iterative = 1
    expand_sequence = graphSearch(matrix, root, 'IDP', iterative)
    while(expand_sequence == 'No Solution'):
        iterative = iterative + 1
        expand_sequence = graphSearch(matrix, root, 'IDP', iterative)
        
    print_solution(expand_sequence, "IDP")
    print("Number of iterations: ", iterative)
    
    # GBFS
    expand_sequence = graphSearch(matrix, root, 'GBFS', 0)
    print_solution(expand_sequence, "GBFS")
    print("")

    # A*
    expand_sequence = graphSearch(matrix, root, 'A*', 0)
    print_solution(expand_sequence, "A*")
    print("")
    pass