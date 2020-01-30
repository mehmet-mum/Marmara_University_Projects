story_file="$1"

file_exist=0

yourfilenames=`ls ./`

for eachfile in $yourfilenames
do
	if [ $eachfile == $story_file ] # check file existing
    then    
    file_exist=1
	fi
done

if [ $file_exist -eq 0 ]
then
    echo "$story_file does not exist"
    exit 1
fi    


echo -ne "$story_file is exist.Do you want it to be modified? (y/n): "
read choice

if [[ ( $choice == "n" ) || ( $choice == "N" ) ]]  # if not want to modify
then
    exit
fi

echo -ne > $story_file  # clear story file
file_name="giris.txt"

string=""
file_count="1"
counter="0"
random=$(( $RANDOM % 3 ))  # generate random number
for (( i=0; i<3; i++))
do
    counter="0"
    random=$(( $RANDOM % 3 ))
    
    if [ $random -eq 1 ]        # skip blanked line
    then
        random=$(( random + 1 ))
    elif [ $random -eq 2 ]      # skip blanked line
    then
        random=$(( random + 2 ))
    fi

   
    while read -r line 
    do
        if [ $counter -eq $random ]
        then
            string=$line
        fi
        counter=$((counter + 1))
    done < "$file_name"
    echo "$string" >> $story_file
    echo "" >> $story_file          # write to line into the story file
    
    if [ $file_count -eq 1 ]
    then
        file_name="gelisme.txt"     # next file
    elif [ $file_count -eq 2 ]
    then
        file_name="sonuc.txt"       # next file
    fi

    file_count=$(( file_count + 1 ))
done


echo "A random story is created and stored in $story_file."
