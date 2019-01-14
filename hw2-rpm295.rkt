(define foldr
  (lambda (f s L)
    (if (null? L)
        s
        (f (car L) (foldr f s (cdr L))))))

(define paramreverse
  (lambda (F AL)
    (if (null? (cdr AL))
        (car AL)
        (F (paramreverse F (cdr AL)) (car AL)))))

(define highest 
  (lambda (L k)
    (let ((M (maximum L)))
        (if (= k 1)
            (list M)
        (append (list M) (highest (remv M L) (- k 1)))))))

; maximum takes a list as a parameter and returns the largest element

(define maximum 
  (lambda (L)
      (if (null? (cdr L))
        (car L)
        (if (>= (car L) (maximum (cdr L)))
            (car L)
            (maximum (cdr L))))))

; remv removes takes an element and a list and returns a list with one
; copy of the element removed

(define remv
  (lambda (x ls)
    (cond
      ((null? ls) '())
      ((eqv? (car ls) x) (cdr ls))
      (else (cons (car ls) (remv x (cdr ls)))))))

(define mapfun 
  (lambda(FL L)
    (if (or (null? (cdr FL)) (null? (cdr L)))
        (list ((car FL) (car L)))
        (cons ((car FL) (car L)) (mapfun (cdr FL) (cdr L))))))

(define filter
  (lambda (pred L)
    (if (null? (cdr L))
        (if (pred (car L))
            (list (car L))
            '())
        (if (pred (car L))
            (cons (car L) (filter pred (cdr L)))
            (filter pred (cdr L))))))