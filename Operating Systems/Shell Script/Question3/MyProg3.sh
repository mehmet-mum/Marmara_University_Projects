
yourfilenames=`ls ./`
writable_exist=0
number_of_moved_files=0
for eachfile in $yourfilenames
do
    if [ $eachfile == writable ]
    then
        writable_exist=1
    fi
done

if [ $writable_exist -eq 0 ]        # if writable directory is not exist then create
then
    mkdir writable
fi

# look each file in directory	
for eachfile in $yourfilenames
do
	if [ -f $eachfile ] # if it is a file do below
	then	
		if [ -w $eachfile ] && [ $eachfile != "MyProg3.sh" ]      # if file is writable then ask to delete it

		then
            mv $eachfile writable
            number_of_moved_files=$(( number_of_moved_files + 1 ))
		fi
	fi
done

echo "$number_of_moved_files files moved to writable directory."
