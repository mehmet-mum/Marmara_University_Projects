 while :
  do
     clear
     echo "-------------------------------------"
     echo " Main Menu "
     echo "-------------------------------------"
     echo "[1] Chipher word"
     echo "[2] Create story"
     echo "[3] Move files"
     echo "[4] Convert hexademical"
     echo "[5] Delete files"
     echo "[6] Exit"
     echo "======================="
     echo -n "Enter your menu choice [1-6]: "
     read yourch
     case $yourch in
        1) 
		cd Question1
		echo "Enter the string"
		read string
		
		echo "Enter number"
		read number
		./MyProg1.sh $string $number 
		cd ..		
		echo "press a key.."; read;;
		
		
 	2) 
		cd Question2
		echo "Enter file"
		read file
		
				
		./MyProg2.sh $file
		
		want_it="n"
		yourfilenames=`ls ./`
		for eachfile in $yourfilenames
        do
            if [ $eachfile == $file ] # if it is a file do below
            then 
                echo -ne "Do you want to read story? (y/n): "
                read want_it
            fi
        done
		
		if [ $want_it == "y" ]
		then
            cat $file
        fi
		cd ..		
		echo "press a key.."; read ;;
		

 	3) 
		cd Question3
		./MyProg3.sh
		cd ..		
		echo "press a key.."; read ;;
		
		 
 	4) 
        cd Question4
        echo "Enter number"
        read number
		
		./MyProg4.sh $number
		cd ..		
		echo "press a key.."; read ;;
		

 	5)
		cd Question5
		echo "Enter a pattern"
		read input
		input=${input:1:2}
		echo -ne "Do you want to give directory: ? (y/n)"
		read want
		if [ $want == "y" ]
		then
            echo "Enter a directory if you wish"
            read input2
            ./MyProg5.sh "$input" $input2
		elif [ $want == "n" ]
		then
            ./MyProg5.sh "$input"
        fi
		cd ..		
		echo "press a key.."; read ;;
		

	6)
		exit 0;;
 	*) echo "Opps!!! Please select choice 1,2,3,4, or 5";
 	   echo "Press a key. . ." ; read ;;
     esac
  done
