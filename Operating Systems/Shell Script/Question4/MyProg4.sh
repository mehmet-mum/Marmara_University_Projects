number=$1
length_of_number=${#number}

for (( i=0; i<length_of_number; i++))                   # check paramater does not contain char 
	do
		s_char=$( printf "%d" "'${number:i:1}}" )
		if [ $s_char -lt 48 ] || [ $s_char -gt 57 ]
		then
		echo "only integer value is acceptable for paramater and should be bigger than 2"
		exit
		fi
	done
if [ $number -lt 3 ]            # check parameter should be bigger than 2
then
    echo "paramater should be bigger than 2"
fi

for (( i=2; i<number; i++))
do
    counter="2"
    prime="1"
    for (( counter; counter<i; counter++ ))     # find prime number
    do
        remain=$(( i % counter ))
        if [ $remain -eq 0 ]
        then
            prime="0"
        fi
    done
    if [ $prime -eq 1 ]                     # convert hexa demical
    then
        prime_number="$i"
        
        hexadecimal_number=""
        hexa=""
        
        while [ $prime_number -ne 0 ]
        do
            hexa=$(( prime_number % 16 ))
            
            if [ $prime_number -lt 16 ]
            then
                hexa=$prime_number
            fi
            
            prime_number=$(( prime_number / 16 ))
            
            
            
            if [ $hexa -eq 10 ]
            then
                hexa="A"
            elif [ $hexa -eq 11 ]
            then
                hexa="B"
            elif [ $hexa -eq 12 ]
            then
                hexa="C"
            elif [ $hexa -eq 13 ]
            then
                hexa="D"
            elif [ $hexa -eq 14 ]
            then
                hexa="D"
            elif [ $hexa -eq 15 ]
            then
                hexa="F"
            fi
            
            hexadecimal_number="$hexa$hexadecimal_number"
            
                
            
        done
        echo "Hexademical of $i is $hexadecimal_number"         # print 
    fi
done
