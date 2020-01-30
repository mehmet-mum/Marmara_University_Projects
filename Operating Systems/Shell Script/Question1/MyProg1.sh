string="$1"
number="$2"

length_of_string=${#string}        
length_of_number=${#number}


    # the first parameter should contain nothing but the character
	for (( i=0; i<length_of_string; i++))
	do
		s_char=$( printf "%d" "'${string:i:1}}" )
		if [ $s_char -lt 61 ] || [ $s_char -gt 122 ]
		then
		echo "only characters are acceptable for first paramater"
		exit
		fi
	done



if [ $length_of_string -eq $length_of_number ]
then

	for (( i=0; i<length_of_string; i++))
	do
	
		s_char=$( printf "%d" "'${string:i:1}}" )  # conver the character its ascii value
		ascii_char=$((s_char + ${number:i:1}))    # add ascii value by giving number on second parameter
		if [ $ascii_char -gt 122 ]                # overflow condition return beginning of alphabet
		then
			ascii_char=$((ascii_char - 26))
		fi
		s_char=$(printf \\$(printf '%03o' $ascii_char))    # conver ascii value to character
		echo -ne "$s_char"                                #print character

	done
fi


if [ $length_of_number -eq 1 ]
then
	for (( i=0; i<length_of_string; i++))
	do
	
		s_char=$( printf "%d" "'${string:i:1}}\n" )  # conver the character its ascii value
		ascii_char=$((s_char + ${number:0:1}))       # add ascii value by giving number on second parameter
		if [ $ascii_char -gt 122 ]                    # overflow condition return beginning of alphabet
		then
			ascii_char=$((ascii_char - 26))
		fi
		s_char=$(printf \\$(printf '%03o' $ascii_char))   # conver ascii value to character
		echo -ne "$s_char"                                    # print character

	done
	
fi
echo " "

if [ $length_of_number -ne 1 ] && [ $length_of_string -ne $length_of_number ]
then
		echo -e "Second parameter should be one digit or equal digit with length of the string"
fi


if [ $# -eq 0 ] # entered no paramater
then
	echo -e "You did not enter paramaters!"
	exit 1
fi

