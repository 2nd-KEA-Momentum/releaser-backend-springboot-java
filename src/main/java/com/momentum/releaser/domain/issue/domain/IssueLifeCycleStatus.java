package com.momentum.releaser.domain.issue.domain;

public enum IssueLifeCycleStatus {
    NOT_STARTED {
        @Override
        public String toString() {
            return "Not started";
        }
    },

    IN_PROGRESS {
        @Override
        public String toString() {
            return "In progress";
        }
    },

    DONE {
        @Override
        public String toString() {
            return "Done";
        }
    },

    COMPLETED {
        @Override
        public String toString() {
            return "Completed";
        }
    },
}
