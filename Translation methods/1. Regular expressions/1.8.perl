while (<>) {
	print if /\([^()]*\w+[^()]*\)/;
}