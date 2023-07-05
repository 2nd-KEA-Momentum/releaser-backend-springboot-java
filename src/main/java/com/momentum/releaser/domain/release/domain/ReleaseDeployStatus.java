package com.momentum.releaser.domain.release.domain;

public enum ReleaseDeployStatus {
    PLANNING {
        @Override
        public String toString() {
            return "배포 예정";
        }
    },

    DEPLOYED {
        @Override
        public String toString() {
            return "배포 허가";
        }
    },

    DENIED {
        @Override
        public String toString() {
            return "배포 거부";
        }
    },
}
