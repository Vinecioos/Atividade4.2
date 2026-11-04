package com.petcare.util;

import com.petcare.dto.AtendimentoDTO;
import org.springframework.web.util.HtmlUtils;

import java.time.format.DateTimeFormatter;

/**
 * Gera a versão imprimível (HTML) de um atendimento.
 * Todo texto vindo do usuário é escapado para evitar injeção de HTML/JS.
 */
public final class AtendimentoDocumento {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private AtendimentoDocumento() {
    }

    public static String html(AtendimentoDTO.Response a) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>Atendimento #%d - %s</title>
                  <style>
                    body { font-family: Arial, Helvetica, sans-serif; color: #222; max-width: 800px; margin: 32px auto; padding: 0 16px; }
                    h1 { font-size: 22px; margin: 0; }
                    .sub { color: #666; margin-bottom: 24px; }
                    table { width: 100%%; border-collapse: collapse; margin-bottom: 20px; }
                    th, td { text-align: left; padding: 6px 8px; border: 1px solid #ccc; font-size: 14px; }
                    th { background: #f2f2f2; width: 30%%; }
                    h2 { font-size: 16px; border-bottom: 2px solid #333; padding-bottom: 4px; margin-top: 24px; }
                    .texto { font-size: 14px; line-height: 1.5; }
                    .assinatura { margin-top: 60px; text-align: center; font-size: 13px; }
                    .assinatura div { border-top: 1px solid #000; width: 320px; margin: 0 auto 4px; }
                    button { margin-bottom: 16px; padding: 8px 16px; font-size: 14px; cursor: pointer; }
                    @media print { button { display: none; } body { margin: 0; } }
                  </style>
                </head>
                <body>
                  <button onclick="window.print()">Imprimir</button>
                  <h1>PetCare Digital Solutions - Registro de Atendimento</h1>
                  <div class="sub">Atendimento nº %d &middot; registrado em %s</div>

                  <table>
                    <tr><th>Animal</th><td>%s (%s)</td></tr>
                    <tr><th>Tutor</th><td>%s</td></tr>
                    <tr><th>Veterinário</th><td>%s &middot; CRMV %s</td></tr>
                    <tr><th>Local</th><td>%s</td></tr>
                    <tr><th>Data da consulta</th><td>%s</td></tr>
                  </table>

                  <h2>Diagnóstico</h2>
                  <div class="texto">%s</div>

                  <h2>Observações</h2>
                  <div class="texto">%s</div>

                  <h2>Orientações ao tutor</h2>
                  <div class="texto">%s</div>

                  <div class="assinatura"><div></div>%s &middot; CRMV %s</div>
                </body>
                </html>
                """.formatted(
                a.id(), esc(a.animalNome()),
                a.id(), a.registradoEm().format(DATA_HORA),
                esc(a.animalNome()), esc(a.especie().name()),
                esc(a.tutorNome()),
                esc(a.veterinarioNome()), esc(a.crmv()),
                esc(a.local()),
                a.dataConsulta().format(DATA_HORA),
                bloco(a.diagnostico()), bloco(a.observacoes()), bloco(a.orientacoes()),
                esc(a.veterinarioNome()), esc(a.crmv()));
    }

    private static String esc(String texto) {
        return HtmlUtils.htmlEscape(texto == null ? "" : texto);
    }

    /** Escapa o texto e preserva quebras de linha; campo vazio vira um traço. */
    private static String bloco(String texto) {
        if (texto == null || texto.isBlank()) {
            return "&mdash;";
        }
        return esc(texto).replace("\r\n", "\n").replace("\n", "<br>");
    }
}
