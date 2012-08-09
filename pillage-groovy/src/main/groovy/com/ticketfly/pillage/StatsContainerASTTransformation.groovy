package com.ticketfly.pillage;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation
/*
@GroovyASTTransformation(phase=CompilePhase.CONVERSION)
class StatsContainerASTTransformation implements ASTTransformation {

	@Override
	public void visit(ASTNode[] nodes, SourceUnit source) {
		List classes = SourceUnit.ast?.classes
		classes.each { ClassNode clazz ->
			if( clazz.equals( StatsContainer.class )){
				clazz.addMethod(makeTimeMethod())	
			}
		}

	}
	
	MethodNode makeTimeMethod() {
		def ast = new AstBuilder().buildFromSpec {
			method('time', ACC_PUBLIC, Long.TYPE){
				parameters {
					parameter 'timerName' :String.class
					parameter 'closure' :Closure.class
				}
				exceptions {}
				block {
					expression {
						declaration {
							variable 'timer' :Timer.class
							token "="
							methodCall {
								variable 'this'
								contstant 'getTimer' :String.class
								argumentList {
									variable {
										parameter 'timerName' :String.class
									}
								}
							}
						}
					}
					expression {
						binary {
							property {
								variable {
									parameter 'closure' :Closure.class
								}
								constant 'delegate' :String.class
							}
							token "="
							variable 'timer' :Timer.class
						}	
					}
					expression {
						binary {
							property {
								variable {
									parameter 'closure' :Closure.class
								}
								constant 'resolverStrategy' :String.class
							}
							token '='
							property {
								classNode Closure
								constant 'DELEGATE_FIRST' :String.class
							}
						}
					}
					expression {
						methodCall {
							variable 'timer' :Timer.class
							constant 'start' :String.class
							argumentList {}
						}
					}
					expression {
						methodCall {
							variable "this"
							constant "closure"
							argumentList {}	
						}
					}
					expression {
						methodCall {
							variable "timer" :Timer.class
							constant "stop" :String.class
							argumentList {}
						}
					}	
				}
			}
			
		}
	}

}
*/