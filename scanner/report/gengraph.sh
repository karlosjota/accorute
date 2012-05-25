#!/bin/bash
rm -rf *.png
for i in `ls *.dot` ; do
    dot $i -Tpng -o ${i%.dot}.png &
done
wait
