package com.challenge.project_api.domain.enums;

public enum ProjectStatus {
    EM_ANALISE,
    ANALISE_REALIZADA,
    ANALISE_APROVADA,
    INICIADO,
    PLANEJADO,
    EM_ANDAMENTO,
    ENCERRADO,
    CANCELADO;

    public boolean canTransitionTo(ProjectStatus next) {
        if (next == CANCELADO) return true;
        
        return switch (this) {
            case EM_ANALISE -> next == ANALISE_REALIZADA;
            case ANALISE_REALIZADA -> next == ANALISE_APROVADA;
            case ANALISE_APROVADA -> next == INICIADO;
            case INICIADO -> next == PLANEJADO;
            case PLANEJADO -> next == EM_ANDAMENTO;
            case EM_ANDAMENTO -> next == ENCERRADO;
            case ENCERRADO, CANCELADO -> false;
        };
    }
}
