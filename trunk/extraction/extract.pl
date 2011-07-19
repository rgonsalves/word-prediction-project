#!/usr/bin/perl 


my $line ='';
my $line_number;

while ($line = <>){#defined ($line = <@raw_data>)) {
  my $boundary = undef;
  my $boundary_alt = '';
  my $boundary_alt_first = undef;
  my $to = '';
  my @content = '';
  my $boundary_first = undef;
  $line_number = $.;
  while ($line = <>){ #each eml
  	$line_number = $.;	
    if($line !~ /^>/){
      if ((!$boundary && !$boundary_alt) & $line =~ /^To:\s+(.+?)$/){
        ($to) = $1;
      }
      else{
        if (!$boundary_first){
	          if($line =~ /^Content-Type:\s+?.+?boundary=\s*(.+?)\r$/ ){
		        	
				    	($boundary) = $1;
	          }
	          else{	
	          	  	if($boundary){
	          	  		if($line =~ /^--($boundary)\r$/){
	          	  			$boundary_first = 1;
				              while ($line = <>){
				              	$line_number = $.;
				                if ($boundary_alt_first){
				                	last if ($line =~ /^--($boundary_alt)\r$/) ;
				                } else{
				                	if ($boundary_first){
				                		last if ($line =~ /^--($boundary)\r$/) ;
				                	}
				                }
				                if($line =~ /^Content-Type:\s+?.+?alternative;.+?boundary=\s*(.+?)\r$/){#alternative on
							    	($boundary_alt) = $1;
				              	} else{
					                if($boundary_alt !~'' && $line =~ /^--($boundary_alt)\r$/){
		              		 			$boundary_alt_first = 1;
		              		 			@content = '';
					                } else{
						                if($line !~ /^>|^Content-.+:.+$|^--$boundary\r$|^--$boundary_alt\r$/){
						                  push @content, $line;
						                }
					                }
				              	}
				              }
	          	  		}
	          	  	}
	          }
       	  	}
#		    else{ 	
#		    	 last if ($line =~ /^--($boundary_alt)--\r$/) ;
#		    }
        }
      }#end if
      if ($line =~ /^--($boundary)--\r$/){
	   	 last;
      }
    }#end while
#    if ($line =~ /^--($boundary_alt)--\r$/){
#    	print "to:$to\n";
#		print "boundary::$boundary\n";
#		print "**********content***********\n";
#		print "@content";
#    }
#    else{
	   if ($line =~ /^--($boundary)--\r$/){
#	   	 last;
		  print "to:$to\n";
		  print "boundary::$boundary\n";
		  print "**********content***********\n";
		  print "@content";
	   }
#    }
}
