#!/bin/bash

correct=0

# afiseaza scorul final
function show_score {
	echo ""
	echo "Scor: $correct"
}

# se compara output-urile din doua directoare (parametri: director1 director2 exponent)
function compare_outputs {
    diff -q $1 $2
    if [ $? == 0 ]
    then
        correct=$((correct+1))
    else
        echo "W: Exista diferente intre fisierul $1 si fisierul $2"
    fi
}

# se compileaza tema
make clean &> /dev/null
make build &> build.txt

if [ ! -f Tema2.class ]
then
	echo "E: Nu s-a putut compila tema"
	cat build.txt
	show_score
	rm -rf build.txt
	exit
fi

rm -rf build.txt

no_threads=("2" "4" "4" "8" "8" "12" "16" "18" "20" "24")
mkdir output
for i in `seq 0 9`
do
	echo ""
	echo "======== Testul ${i} ========"
	echo ""

    mkdir output_${i}

    java Tema2 input/input_${i} ${no_threads[$i]}

	# se verifica daca rezultatele sunt corecte
    sort orders_out.txt
    compare_outputs output/output_${i}/orders_out.txt orders_out.txt
    sort order_products_out.csv
    compare_outputs output/output_${i}/order_products_out.txt order_products_out.txt

	echo "OK"
	echo ""

	echo "=========================="
	echo ""
done

rm -rf output
make clean &> /dev/null

show_score