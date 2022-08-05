
>**Completed: Mar 23, 2022*

### Simplified A-Priori Algorithm

The A-Priori algorithm utilizes the subset property for frequent itemsets to enable significant pruning of the space for possible itemset combinations. Using all possible 2-partitions of the itemset, where neither partition is empty, generates all rules for the set. Execution of armin.py generates only the rules whose support and confidence is greater or equal to the min support percentage and min confidence. Assuming user-given min support percentage and min confidence,the script conducts process starting with CFI(1) and takes Step i until terminating on step k, when CFI(k+1) is empty. The following describes i-th step:

**Step i:**  

• Consider all frequent itemsets of size i. Denoted as CFI(i).  
• Generate support percentage from support count for CFI(i)
• Support percentage of CFI(i) greater than or equal to min support percentage become verified frequent itemsets VFI(i).  
• Using subset property, generate all plausible candidate itemsets of size CFI(i + 1) from itemsets in VFI(i) .

---

> **Input Format**
>* input.csv as test input file 

> Notes:  
> * No spaces/puncuation characters in item names.   
> * Address presence of whitespace in input csv.

> **Output Format** Outputs a CSV file where:  
>`S,support_percentage,itemA,itemB,itemC, ...` denotes a frequent item**s**et
>`R,support_percentage,confidence,itemD,itemE, ...,’=>’,itemF,itemG, ...  ` denotes an association **r**ule. 

> Lists all items in the frequent itemset case, left, and right of the => sign in the association rule case in lexicographic order.
> `support_percentage` 
> denotes support % of the specific frequent itemset/association rule greater than user-specified min_support_percentage.
> `confidence` denotes confidence percentage for specific association rule greater than user-specified min_confidence.  
> Output file lists all frequent itemsets association rules for given input file using the A-Priori method passing the min support percentage + min confidence thresholds.  
>

---

