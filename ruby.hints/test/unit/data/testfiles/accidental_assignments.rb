foo = 5
bar = 6
puts "equal" if foo = bar

if (foo = bar)
  puts "Equal!"
else
  puts "Not Equal!"
end

if foo = bar # comment
   puts "equal"
end
# Make sure that "new" assignments aren't flagged
if (newvar = bar)
   puts "done"
end

