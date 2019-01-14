(* add the head of the list to the result of calling listsum
on the tail of the list *)
fun listsum [] = 0.0 | listsum(x::xs) = x + listsum(xs);

(* add 1 upon each iteration of fold *)
fun length L = foldl (fn (_,b) => b + 1) 0  L;

(* use previous 2 fuctions plus conversion *)
fun mean L = (listsum L) / Real.fromInt(length L);

fun reverse L = foldl (fn (a,b) => a :: b) nil L;

(* account for cases where each or both lists may be empty else
take the head of first and cons with recursive call swapping args *)
fun interleave nil nil = nil 
    | interleave nil (y::ys) = y :: (interleave nil ys) 
    | interleave (x::xs) nil = x :: (interleave xs nil) 
    | interleave (x::xs) (y::ys) = x :: (interleave (y::ys) xs); 

(* helper method for splitif*)
fun filter nil f = nil
    | filter (x::xs) f = 
        if f x = true then x :: filter xs f
        else filter xs f;

(* form a tuple of two lists, the first the result of calling filter
on f the second calling filter on not f *)
fun splitif L f = ((filter L f), (filter L (not o f)));

(* if ever one list is empty when the other is not throw excep else
apply binary function to heads of each list and recurse *)
exception Mismatch;
fun dobinary nil nil f = nil 
    | dobinary nil (y::ys) f = raise Mismatch 
    | dobinary (x::xs) nil f = raise Mismatch
    | dobinary (x::xs) (y::ys) f = (f (x, y)) :: (dobinary xs ys f);

(* if list is empty return [i], else return accumulator i appended to result
of calling scan_left on f, f applied to i and the head of the list (which 
returns new i) and the tail *)
fun scan_left f i nil = [i]
    | scan_left f i (x::xs) = i :: (scan_left f (f i x) xs);

fun countup n =
    let fun countup' 0 l = l
        | countup' i l = countup' (i - 1) (i::l)
            in
                    countup' n []
        end;

(* get the list of numbers from 1 to n by calling countup, take its tail
so 1 is not listed twice, then pass it as a list to scan left using function
op* and 1, this will keep an accumlated total of the factorial values and 
return it as a list *)
fun fact_list n = scan_left (fn x => (fn y => x*y)) 1 (tl(countup n));