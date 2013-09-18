;;; Redefine add-to-global-vars so it doesn't require any of the define-form stuff
(define add-to-global-vars
  (lambda (name :: gnu.mapping.Symbol thunk)
    (gnu.mapping.Environment:put *test-environment* name (thunk))))

(define (stringTest1)
  (and
    (equal? "12" (call-yail-primitive string-append (*list-for-runtime* 1 2 )
                                                   '( text text)
                                                      "make text"))
    (equal? 3 (call-yail-primitive + (*list-for-runtime* "1" 2)
                                      '( number number)
                                      "+"))))



;; (define (testCallWithNoCoercions)
;;   (equal? (call-yail-primitive list (*list-for-runtime* 1 2 'x) '() "make list")
;;           '(1 2 x)))

(define (testDefOfVariable)
  (def foo "bar")
  (equal? "bar" (get-var foo)))

(define (testDefOfProcedure)
  (def (foo x) x)
  (equal? "baz" ((get-var foo) "baz")))



(define (testTailRecursion)
 (def (tailRecAdd a b )
   (if
    (yail-equal?  (lexical-value a)  0)
    (begin (lexical-value b) )
    (begin ( (get-var tailRecAdd)
             (call-yail-primitive
              -
              (*list-for-runtime* (lexical-value a)  1)
              '( number number)
              "-")
             (call-yail-primitive
              +
              (*list-for-runtime* (lexical-value b)  1)
              '( number number)
              "+") )) ) )
 (= ((get-var tailRecAdd) 800 800) 1600)
 ;; There's something funny about how Kawa is handling tail recursion
 ;; The test above with a=800 works, but the test with a=900 gets a
 ;; stack overflow.
 ;; (= ((get-var tailRecAdd) 900 900) 1800)
 ;; On the other hand, this code runs on the phone with a=10,000,
 ;; That computation takes about 18 seconds.
)


;; Support for testing repl communication


(define (last-response) *last-response*)

(set! *testing* #t)

