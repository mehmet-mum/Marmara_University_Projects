import time
start_time = time.time()

f1 = open("NEPAL.txt", "r")
f2 = open("WUHAN.txt", "r")
NEPAL = f1.readline()
f1.close()
WUHAN = f2.readline()
f2.close()

nucleotide_map = {
    "a": "t",
    "c": "g",
    "g": "c",
    "t": "a"
}

#get reverse compliment of k-mer
def reverse_compliment_k_mer(k_mer):
    res = ""
    k = len(k_mer)
    for i in range(k-1, -1, -1):
        res += nucleotide_map[k_mer[i]]
    return res

# calculate hamming distance
def hamming_distance(str1, str2, d):
    i = 0
    missmatches = 0
    while True:
        if str1[i] != str2[i]:
            missmatches += 1
        
        i += 1

        # if number of mismatches does not exceed the given d value until end of the strings return True
        # when the mismatches exceed then return False
        if missmatches > d:
            return False
        elif i == len(str1):
            return True

        
    
# calculate number of occurences with hamming distance
def calculate_occurences(string, k_mer, rev_comp_k_mer, k, d):
    count = 1
    text_size = len(string) - k
    for i in range(0, text_size):
        sub_str = string[i: i+k]
        
        # check hamming distance
        if hamming_distance(k_mer, sub_str, d):
            count += 1
        elif hamming_distance(rev_comp_k_mer, sub_str, d):
            count += 1

    return count

# calculate number of occurences without hamming distance
# this algorithm run faster but it can not calculate hamming distance
# so use this algorithm for hamming distance 0
def count_occurrences(string, substring):
    count = 0
    start = 0
    while start < len(string):
        flag = string.find(substring, start)
        if flag != -1:
            start = flag + 1
            count += 1
        else:
            return count



def find_most_frequent_kmers(text, k, d):
    looked_kmers = []   # keep looked kmers in here
    
    frequent_k_mer = ""
    reverse_comp_k_mer = ""
    number_of_occurence = 0
    text_size = len(text) - k


    for i in range(0, text_size):
        k_mer = text[i: i + k]

        # if a k-mer is looked before skip it
        if k_mer in looked_kmers:
            continue

        # take reverse compliment of k-mer
        rev_comp_k_mer = reverse_compliment_k_mer(k_mer)

        # if hamming distance is 0  go function which does not calculates hamming distance
        if d == 0:
            k_mer_freq = count_occurrences(text[i:], k_mer)
            k_mer_freq += count_occurrences(text[i:], rev_comp_k_mer)
        
        # else go function which calculates hamming distance
        else:
            k_mer_freq = calculate_occurences(text[i+1:], k_mer, rev_comp_k_mer, k, d)
        
        
        # if k-mer frequenct is bigger than number of occurance of the most freq. found so far
        # update it 
        if k_mer_freq > number_of_occurence:
            number_of_occurence = k_mer_freq
            frequent_k_mer = k_mer
            reverse_comp_k_mer = rev_comp_k_mer


        # append k-mer and it's reverse compliment to list
        looked_kmers.append(k_mer)
        looked_kmers.append(rev_comp_k_mer)

    return frequent_k_mer, reverse_comp_k_mer,number_of_occurence


def find_all_frequent_k_mers(place,text,d):
    k_length = 5
    print("{}:          Hamming Distance:{}".format(place,d))
    print("--------------------------------------------------")

    # find the most frequent k-mers from k is 5 to until no frequency
    while True:
        freq_kmer, rev_kmer, number_of_occu = find_most_frequent_kmers(WUHAN, k_length, d)

        # if there is no frequent k-mer stop the loop
        if number_of_occu == 1:
            break;

        print("{}-mer".format(k_length))
        print("------------------")
        print("k-mer:    {}".format(freq_kmer))
        print("rev comp: {}".format(rev_kmer))
        print("freq:     {}\n".format(number_of_occu))
        
        k_length += 1


if __name__ == "__main__":
    
    # find the most frequent k-mers with different hamming distances
    find_all_frequent_k_mers("WUHAN",WUHAN,0)
    find_all_frequent_k_mers("WUHAN",WUHAN,1)
    find_all_frequent_k_mers("WUHAN",WUHAN,2)


    find_all_frequent_k_mers("NEPAL",NEPAL,0)
    find_all_frequent_k_mers("NEPAL",NEPAL,1)
    find_all_frequent_k_mers("NEPAL",NEPAL,2)



    print("--- %s seconds ---" % (time.time() - start_time))
