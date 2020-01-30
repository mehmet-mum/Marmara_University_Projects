file_name=$1
path_name=$2
length=${#file_name}

length=$(( length - 1 ))        # this is for location

deleted_files=0     # number of deleted files

wild=0  # conditions
parameter_number=$#

# no wild card
if [ $( printf "%d" "'${file_name:length:1}}" ) -ne 42 ] &&  [ $( printf "%d" "'${file_name:0:1}}" ) -ne 42 ]
then
    wild=0
# "*...*"
elif [ $( printf "%d" "'${file_name:length:1}}" ) -eq 42 ] &&  [ $( printf "%d" "'${file_name:0:1}}" ) -eq 42 ]
then
    length=$(( length - 1 ))
    file_name=${file_name:1:length}
    wild=1
# "...*"
elif  [ $( printf "%d" "'${file_name:length:1}}" ) -eq 42 ]
then
    file_name=${file_name:0:length}
    wild=2
# "*..."
elif [ $( printf "%d" "'${file_name:0:1}}" ) -eq 42 ]
then
    file_name=${file_name:1:length}
    wild=3
fi


if [ $# -eq 2 ]   # if directory is given go this directory
then
    cd $path_name
fi


function1 () {
if [ $wild -eq 0 ]      # for first condition
then
    
    allfiles=`ls ./`

    for eachfile in $allfiles       # scan each file
    do
          if [ -f $eachfile ] && [ $eachfile == $file_name ] # if file suitable  ask for delete 
            then	
		
	     		echo -ne "Do you want to delete $eachfile: ? (y/n) "    # ask user to delete this file
	     		read string		
	     		if [ $string == "y" ]  # if user enter 'y' then remove the file
	     		then
	           		rm $eachfile
	           		deleted_files=$(( deleted_files + 1 ))
            fi

      		
        fi
        if [ -d $eachfile ] && [ $parameter_number -eq 2 ]  # if directory is given check sub directories
        then
             cd $eachfile
             function1
             cd ..
        fi
    done

###########################################################################
elif [ $wild -eq 1 ]    # check each file to determine it contains the pattern or not
then
    
    
    allfiles=`ls ./`

    for eachfile in $allfiles
    do
                eachfile_length=${#eachfile}
                eachfile_length=$(( eachfile_length - length ))
                for (( i=0; i<=eachfile_length; i++ ))  # at the file name check the pattern by increasing i 
                do
                    if  [ ${eachfile:i:length} == $file_name ] && [ -f $eachfile ]
                    then
                        i=$(( eachfile_length + 1 ))
                        echo -ne "Do you want to delete $eachfile: ? (y/n) "
                        read string		
                        if [ $string == "y" ]
                        then
                            rm $eachfile
                            deleted_files=$(( deleted_files + 1 ))
                        fi
                    fi
                    if [ -d $eachfile ] && [ $parameter_number -eq 2 ]  # if directory is given check sub directories
                    then
                        cd $eachfile
                        function1
                        cd ..
                    fi
                done   
    done


#########################################################################
# if paramater is like  s*,a*,asdsad*  (1 star at the end)
elif  [ $wild -eq 2 ]   # check each file name starts with the pattern or not 
then
        

    allfiles=`ls ./`



    for eachfile in $allfiles
    do
                if  [ ${eachfile:0:length} == $file_name ] && [ -f $eachfile ]
                then
                    echo -ne "Do you want to delete $eachfile: ? (y/n) "
                    read string		
                    if [ $string == "y" ]
                    then
                        rm $eachfile
                        deleted_files=$(( deleted_files + 1 ))
                    fi
                fi
                if [ -d $eachfile ] && [ $parameter_number -eq 2 ]
                    then
                        cd $eachfile
                        function1
                        cd ..
                    fi
    done
##########################################################################
elif [ $wild -eq 3 ]  # check if the file name ends with pattern or not
then
    
    allfiles=`ls ./`



    for eachfile in $allfiles
    do
                eachfile_length=${#eachfile}
                eachfile_length=$(( eachfile_length - length ))
                if  [ ${eachfile:eachfile_length:length} == $file_name ] && [ -f $eachfile ]
                then
                    echo -ne "Do you want to delete $eachfile: ? (y/n) "
                    read string		
                    if [ $string == "y" ]
                    then
                        rm $eachfile
                        deleted_files=$(( deleted_files + 1 ))
                    fi
                fi
                if [ -d $eachfile ] && [ $parameter_number -eq 2 ]
                    then
                        cd $eachfile
                        function1
                        cd ..
                    fi
    done
fi
}

function1
echo "$deleted_files file deleted"
