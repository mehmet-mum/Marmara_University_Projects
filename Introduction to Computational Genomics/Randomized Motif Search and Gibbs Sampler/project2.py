import random
import numpy as np


#generate a random nucleotide
def generate_random_nucleotide():
    random_number = random.randint(0,3)
    if (random_number == 0):
        return 'A'
    elif (random_number == 1):
        return 'T'
    elif (random_number == 2):
        return 'G'
    else:
        return 'C'

# create dna sequence
def create_input_file(number_of_lines, dna_length):
    dna = []
    for i in range(number_of_lines):
        dna_sequence = ''
        for j in range(dna_length):
            dna_sequence += generate_random_nucleotide()

        dna.append(dna_sequence)    

    return dna

# apply mutations to motifs
def apply_mutations(motif_matrix, motif_length, number_of_mutations):
    for i in range(len(motif_matrix)):
        # generate random positions for each motif
        positions = random.sample(range(0, motif_length), number_of_mutations)
        motif_matrix[i] = list(motif_matrix[i])
        for j in positions:
            while True:
                # apply mutation
                mutated_nucleoid = generate_random_nucleotide()

                if (motif_matrix[i][j] != mutated_nucleoid):
                    motif_matrix[i][j] = mutated_nucleoid
                    break
        
        motif_matrix[i] = "".join(motif_matrix[i])

    return motif_matrix

# insert motif into dna
def mutation_on_dna(motif_matrix, positions, dna, motif_length):
    for i in range(len(motif_matrix)):
        dna[i] = list(dna[i])
        dna[i][positions[i]: positions[i] + motif_length] = motif_matrix[i]
        dna[i] = "".join(dna[i])

    return dna

# select random motifs for algorithms
def select_random_motifs(dna, motif_length):
    motif_matrix = []
    positions = random.sample(range(0, len(dna[0]) - motif_length), len(dna))
    for i in range(len(dna)):
        motif_matrix.append(dna[i][positions[i]: positions[i] + motif_length])

    return motif_matrix

# generate count matrix from motif matrix
def count_motifs(motif_matrix, motif_length):
    count = []
    for i in range(motif_length):
        col = []
        number_of_occurences = []
        for motif in motif_matrix:
            col.append(motif[i])

        number_of_occurences.append(col.count('A'))
        number_of_occurences.append(col.count('C'))
        number_of_occurences.append(col.count('G'))
        number_of_occurences.append(col.count('T'))

        count.append(number_of_occurences)
    
    return count

# remove a motif from count matrix ( for gibbs sampler )
def remove_motif_from_count(count, motif):
    for i in range(len(motif)):
        if (motif[i] == 'A'):
           count[i][0] -= 1
        if (motif[i] == 'C'):
           count[i][1] -= 1
        if (motif[i] == 'G'):
           count[i][2] -= 1
        elif (motif[i] == 'T'):
            count[i][3] -= 1
    return count

# calculate score of motifs
def score_of_motifs(count, motif_length):
    score = 0
    for i in count:
        score += sum(i) - max(i)
    
    return score

# generate profile matrix from count
def profile(count):
    profile_matrix = []
    max_number = sum(count[0])
    for i in range(len(count)):
        probs = []
        
        for j in range(4):
            probs.append(count[i][j]/max_number)

        profile_matrix.append(probs)
    
    return profile_matrix

# calculate probability of a single motif
def probability_of_motif(profile_matrix, motif, motif_length):
    prob = 1
    for i in range(len(motif)):
        if (motif[i] == 'A'):
            prob = prob * profile_matrix[i][0]
        elif (motif[i] == 'C'):
            prob = prob * profile_matrix[i][1]
        elif (motif[i] == 'G'):
            prob = prob * profile_matrix[i][2]
        elif (motif[i] == 'T'):
            prob = prob * profile_matrix[i][3]


    return prob
        

# generate best motif matrix according to profile matrix
def motifs(profile_matrix, dna, motif_length):
    motif_matrix = []
    
    for each_dna in dna:
        probs = []
        for j in range(len(each_dna) - motif_length + 1):
            probs.append(probability_of_motif(profile_matrix, each_dna[j:j+motif_length], motif_length))
            
        index = probs.index(max(probs))
        motif_matrix.append(each_dna[index: index + motif_length])
    
    return motif_matrix


# randomized motif search algorithm
def randomized_motif_search(dna, motif_length):
    # get random motif matrix
    motif_matrix = select_random_motifs(dna, motif_length)


    best_motif = motif_matrix
    count = count_motifs(motif_matrix, motif_length)
    # calculate score of the random motif matrix
    best_score = score_of_motifs(count, motif_length)
    while True:
        # get profile matrix
        profile_matrix = profile(count)
        # get new motif matrix from profile matrix
        motif_matrix = motifs(profile_matrix, dna, motif_length)
        
        # get count matrix
        count = count_motifs(motif_matrix, motif_length)
        # calculate the score of new motif matrix
        new_score = score_of_motifs(count, motif_length)

        # if new score is less than best score update best score
        if( new_score < best_score):
            best_motif = motif_matrix
            best_score = new_score
        # if there is improvment than stop algorithm
        else:
            return best_motif, best_score

# get a motif according to gibbs sampler algorithm
def gibbs_sampler_motifs(profile_matrix, dna, motif_length, random_motif):
    
    # calculate the probabilities of each mer in dna
    probs = []
    for j in range(len(dna[random_motif]) - motif_length + 1):
        probs.append(probability_of_motif(profile_matrix, dna[random_motif][j:j+motif_length], motif_length))

    # expand probabilities to 1
    factor = 1/float(sum(probs))
    for i in range(len(probs)):
        probs[i] = probs[i] * factor

    # get random motif according to probability distrubition
    index = np.random.choice(np.arange(0, len(probs)), p=probs)
    return dna[random_motif][index: index + motif_length]
    

def gibbs_sampler(dna, motif_length, n):
    # get random motif matrix 
    motif_matrix = select_random_motifs(dna, motif_length)

    best_motif = motif_matrix
    count = count_motifs(motif_matrix, motif_length)
    # calculate the score of motif matrix
    best_score = score_of_motifs(count, motif_length)
    
    # counter to count iterations
    counter = 0
    # a boolean value to check whether best score improved or not
    changed = False
    while True:
        # generate a random number to change a particular motif 
        random_motif = random.randint(0,len(motif_matrix) - 1)
        # remove that motif from count matrix
        count = remove_motif_from_count(count, motif_matrix[random_motif])

        # increase all elements in the count matrix by 1
        for i in range(len(count)):
            for j in range(len(count[i])):
                count[i][j] += 1

        # generate profile matrix from count matrix
        profile_matrix = profile(count)
        # get new motif from profile matrix 
        motif_matrix[random_motif] = gibbs_sampler_motifs(profile_matrix, dna, motif_length, random_motif)
        
        # calculate count matrix from new motif matrix
        count = count_motifs(motif_matrix, motif_length)
        # calculate new socre of motif matrix
        new_score = score_of_motifs(count, motif_length)

        # if new score is less than best score update best score
        if( new_score < best_score):
            best_motif = motif_matrix
            best_score = new_score
            changed = True

        counter += 1

        # if n is bigger than 0 run the algorithm with given iterations
        if ( n > 0):
            if (counter == n):
                return best_motif, best_score
        else:
            # check algorithm every 50 iterations
            if ( counter % 50 == 0):
                # if best score did not improved then end the algorithm
                if(changed == False):
                    return best_motif, best_score
                
                changed = False

def generate_consensus_string(count):
    consensus_string = ''
    for i in range(len(count)):
        max_element = max(count[i])
        for j in range(4):
            if ( count[i][j] == max_element ):
                if (j == 0):
                    consensus_string = consensus_string + 'A/'
                elif (j == 1):
                    consensus_string = consensus_string + 'C/'
                elif (j == 2):
                    consensus_string = consensus_string + 'G/'
                elif (j == 3):
                    consensus_string = consensus_string + 'T/'
        
        consensus_string = consensus_string[0:len(consensus_string) -1]
        consensus_string = consensus_string + ' '
    
    return consensus_string


if __name__ == "__main__":
    # generate random dna sequence
    dna = create_input_file(number_of_lines = 10, dna_length = 500)

    # get random positions to insert mutated motif
    positions = random.sample(range(0, 490), 10)

    # the 10-mer motif which will be mutated and inserted to known positions in the dna
    motif_matrix = [
        'AAAAACCCCC',
        'AAAAACCCCC',
        'AAAAACCCCC',
        'AAAAACCCCC',
        'AAAAACCCCC',
        'AAAAACCCCC',
        'AAAAACCCCC',
        'AAAAACCCCC',
        'AAAAACCCCC',
        'AAAAACCCCC'
    ]

    # apply mutations to motif
    motif_matrix = apply_mutations(motif_matrix, motif_length = 10, number_of_mutations = 4)
    # insert motif to dna
    dna = mutation_on_dna(motif_matrix, positions, dna, motif_length = 10)


    # get results for randomized motif search with k = 9
    print('Randomized motif search with k = 9')
    print('==========================================')
    best_motif, best_score = randomized_motif_search(dna, 9)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 9)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')
    

    # get results for gibbs sampler with k = 9
    print('Gibbs sampler with k = 9')
    print('==========================================')
    best_motif, best_score = gibbs_sampler(dna, 9, 0)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 9)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')


    # get results for gibbs sampler with k = 9 for n = 100000
    print('Gibbs sampler with k = 9')
    print('==========================================')
    best_motif, best_score = gibbs_sampler(dna, 9, 100000)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 9)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')
    
    #####################################################################################################

    # get results for randomized motif search with k = 10
    print('Randomized motif search with k = 10')
    print('==========================================')
    best_motif, best_score = randomized_motif_search(dna, 10)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 10)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')

    # get results for gibbs sampler with k = 10
    print('Gibbs sampler with k = 10')
    print('==========================================')
    best_motif, best_score = gibbs_sampler(dna, 10, 0)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 10)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')

    # get results for gibbs sampler with k = 10 for 100000 iterations
    print('Gibbs sampler with k = 10')
    print('==========================================')
    best_motif, best_score = gibbs_sampler(dna, 10, 100000)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 10)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')

    #####################################################################################################

    # get results for randomized motif search with k = 11
    print('Randomized motif search with k = 11')
    print('==========================================')
    best_motif, best_score = randomized_motif_search(dna, 11)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 11)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')

    # get results for gibbs sampler with k = 11
    print('Gibbs sampler with k = 11')
    print('==========================================')
    best_motif, best_score = gibbs_sampler(dna, 11, 0)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 11)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')

    # get results for gibbs sampler with k = 11 for 100000 iterations
    print('Gibbs sampler with k = 11')
    print('==========================================')
    best_motif, best_score = gibbs_sampler(dna, 11, 100000)
    
    print('Best score: {}'.format(best_score))
    print('Best motifs:')
    for i in best_motif:
        print(i)
    count = count_motifs(best_motif, 11)
    print('\nConsensus string: {}'.format(generate_consensus_string(count)))
    print('\n\n')