package org.opensbpm.spass;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;

public class Main {

    public static void main(String[] args) throws SPassIncompleteException, OWLOntologyCreationException {
        SPassReader.loadOwl(new File(args[0]));
    }
}
