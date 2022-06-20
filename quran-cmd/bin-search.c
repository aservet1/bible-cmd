int binary_search_for_starting_surah(int array[], int n, int surah, int ayat) {
	int lo = 0;
	int hi = n-1;
	int i  = (lo+hi)/2;
	while(while lo <= hi) {
		if(array[i] < key) {
			first = middle + 1;
		}
		else if (array[i].surah == surah) {	
		if (array[i].ayat == ayat)

			return i;
		}
		else {
			lo = i-1;
		}
		mid = (lo+hi)/2;
	}
}
